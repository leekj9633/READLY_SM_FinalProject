-- 방장(host)을 book_club.host_id 컬럼에 명시적으로 저장하도록 바꾸면서 필요한 기존 데이터 백필.
--
-- 배경:
--   예전에는 방장을 나타내는 컬럼이 없었고, member_book_club.id 가 가장 작은 행의 회원을
--   방장으로 간주했다(BookClubService.createBookClub 이 생성자를 가장 먼저 저장하기 때문).
--   이 암묵적 규칙은 방장이 탈퇴하면 두 번째 가입자가 조용히 방장이 되고, 위임도 불가능했다.
--   이제 BookClub.host (컬럼 host_id) 가 방장을 직접 가리킨다.
--
--   ddl-auto: update 가 host_id 컬럼 자체는 만들어 주지만, 기존 행은 전부 NULL 로 남는다.
--   host_id 가 NULL 인 모임에서는 AI 진행자 호출이 "방장이 지정되지 않은 독서모임입니다."로 막히고,
--   상세 조회의 role 도 전부 PARTICIPANT 가 된다. 아래 스크립트로 한 번 채워 준다.
--
-- 실행 방법:
--   1) 앱을 한 번 기동해 Hibernate 가 book_club.host_id 컬럼을 만들게 한다.
--   2) 아래 스크립트를 readly DB 에 대해 한 번 실행한다.
--      psql -h localhost -U postgres -d readly -f db/2026-08-17-add-bookclub-host.sql
--
--   dev DB 라 날려도 상관없다면, 스키마를 통째로 지우고 앱을 재기동해도 된다
--   (앞으로 만들어지는 모임은 생성 시점에 host_id 가 채워진다).

BEGIN;

-- 앱을 아직 기동하지 않았어도 실행할 수 있도록 컬럼을 먼저 확보한다.
ALTER TABLE IF EXISTS book_club ADD COLUMN IF NOT EXISTS host_id BIGINT;

-- 기존 규칙 그대로, 조인 테이블에서 id 가 가장 작은 행의 회원을 방장으로 채운다.
-- DISTINCT ON 은 PostgreSQL 전용이며, ORDER BY 의 첫 컬럼이 DISTINCT ON 대상과 같아야 한다.
UPDATE book_club bc
SET host_id = first_joined.member_id
FROM (
    SELECT DISTINCT ON (mbc.club_id) mbc.club_id, mbc.member_id
    FROM member_book_club mbc
    ORDER BY mbc.club_id, mbc.id ASC
) AS first_joined
WHERE bc.club_id = first_joined.club_id
  AND bc.host_id IS NULL;

-- 외래키 제약을 붙여 둔다 (이미 있으면 건너뛴다).
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_book_club_host'
    ) THEN
        ALTER TABLE book_club
            ADD CONSTRAINT fk_book_club_host
            FOREIGN KEY (host_id) REFERENCES member (member_id);
    END IF;
END $$;

COMMIT;

-- 확인용: 여전히 방장이 없는 모임이 있는지 본다.
-- 가입자가 한 명도 없는 모임은 채울 근거가 없어 NULL 로 남는다.
SELECT club_id, name
FROM book_club
WHERE host_id IS NULL;
