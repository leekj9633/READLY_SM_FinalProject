import styled from "styled-components";
import { FiSend } from "react-icons/fi";

function GroupDetail() {
  return (
    <Wrap>
      <Header>← 노르웨이의 숲</Header>

      <AIBox>
        AI 질문: 오늘 느낀 감정을 말해보세요
      </AIBox>

      <ChatArea />

      <InputBox>
        <input placeholder="메세지를 입력하세요" />
        <FiSend />
      </InputBox>
    </Wrap>
  );
}

export default GroupDetail;

/* ===== styled ===== */

const Wrap = styled.div`
  padding: 15px;
  background: #F6FBF2;
`;

const Header = styled.div`
  font-weight: bold;
`;

const AIBox = styled.div`
  margin-top: 20px;
  padding: 15px;
  background: #8BC34A;
  color: white;
  border-radius: 12px;
`;

const ChatArea = styled.div`
  height: 300px;
`;

const InputBox = styled.div`
  position: fixed;
  bottom: 10px;
  width: 90%;
  display: flex;
  background: white;
  padding: 10px;
  border-radius: 12px;

  input {
    flex: 1;
    border: none;
    outline: none;
  }
`;