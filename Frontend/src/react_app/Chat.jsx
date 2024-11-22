import React, { useEffect } from "react";
import SendbirdApp from "@sendbird/uikit-react/App";
import "@sendbird/uikit-react/dist/index.css";
import withSendbird from "@sendbird/uikit-react/withSendbird";
import sendbirdSelectors from "@sendbird/uikit-react/sendbirdSelectors";

const Chat = (props) => {
  const sdk = sendbirdSelectors.getSdk(props);
  const currentUser = sdk && sdk.currentUser;

  // REST API 호출 함수: 사용자 목록 가져오기
  const fetchUserList = async () => {
    const url = `https://api-${props.config.APP_ID}.sendbird.com/v3/users`;

    try {
      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Api-Token": props.config.API_TOKEN, // API Token 포함
        },
      });
      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }

      const data = await response.json();
      console.log("User List:", data); // 사용자 목록 출력
    } catch (error) {
      console.error("Failed to fetch user list:", error);
    }
  };

  // 컴포넌트 렌더링 후 REST API 호출
  useEffect(() => {
    if (currentUser) {
      fetchUserList();
    }
  }, [currentUser]);

  if (currentUser) {
    // 전체 읽지 않은 메시지 수 가져오기
    const getTotalUnreadMessageCount = async () => {
      const unreadMessageCount =
        await sdk.groupChannel.getTotalUnreadMessageCount();
      props.setUnreadMessageCount(unreadMessageCount);
    };
    getTotalUnreadMessageCount();
    props.setSbUserInfo(currentUser);
  }

  // Sendbird UI Kit 컴포넌트 렌더링
  return (
    <SendbirdApp
      appId={props.config.APP_ID}
      userId={props.config.USER_ID}
      nickname={props.config.NICKNAME}
      breakpoint={true}
    />
  );
};

// withSendbird로 컴포넌트 래핑
const ChatWithSendbird = withSendbird(Chat);
export default ChatWithSendbird;
