import React from "react";
import SendbirdApp from "@sendbird/uikit-react/App";
import { useEffect } from "react";
import "@sendbird/uikit-react/dist/index.css";

const Chat = (props) => {
  const myColorSet = {
    "--sendbird-light-primary-500": "#4d2bf0",
    "--sendbird-light-primary-400": "#6440ff",
    "--sendbird-light-primary-300": "#7b5dff",
    "--sendbird-light-primary-200": "#9783ff",
    "--sendbird-light-primary-100": "#b2a4ff",
    "--sendbird-light-background-50": "#F8FAFF",
    "--sendbird-light-background-100": "#EEF2FF",
    "--sendbird-light-background-200": "#E5EEFF",
    "--sendbird-light-onlight-01": "#000000",
    "--sendbird-light-onlight-02": "#2C2C2C",
    "--sendbird-light-onlight-03": "#515151",
    "--sendbird-light-onlight-04": "#737373",
  };

  const myStringSet = {
    CHANNEL__MESSAGE_INPUT__PLACE_HOLDER: "메시지를 입력하세요.",
    CHANNEL_LIST__TITLE: "채팅",
    CHANNEL__MESSAGE_LIST__LOADING: "메시지를 불러오는 중...",
    CHANNEL__MESSAGE_LIST__NOTHING_TO_SHOW: "메시지가 없습니다.",
  };

  return (
    <div className="channel-wrap" style={{ height: "100%" }}>
      <SendbirdApp
        appId={props.config.APP_ID}
        userId={props.config.USER_ID}
        nickname={props.config.NICKNAME}
        profileUrl={props.config.PROFILE_URL}
        colorSet={myColorSet}
        stringSet={myStringSet}
        breakpoint={true}
        theme="light"
        config={{
          enableEnterToSubmit: true,
          isOnline: true,
        }}
      />
    </div>
  );
};

export default Chat;
