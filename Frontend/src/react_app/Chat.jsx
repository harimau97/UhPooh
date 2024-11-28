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

  useEffect(() => {
    const style = document.createElement("style");
    style.textContent = `
      .sendbird-channel-list {
        width: 100% !important;
      }
      .sendbird-channel-list__body {
        width: 100% !important;
      }
      .sendbird-channel-preview {
        width: 100% !important;
      }
      .sendbird-channel-header {
        width: 100% !important;
      }
      .sendbird-channel-header__title {
        width: 100% !important;
      }
      
      .sendbird-modal__content {
        width: 520px !important;
        height: 50% !important;
      }
       
      .sendbird-channel-settings-header {
        width: 100% !important;
      }
      .sendbird-channel-header__right-icon {
        right: 16px !important;
      }
    `;
    document.head.appendChild(style);
    return () => {
      document.head.removeChild(style);
    };
  }, []);

  return (
    <div
      className="channel-wrap"
      style={{
        width: "100%",
        height: "100%",
        position: "relative",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <div style={{ flex: 1, width: "100%" }}>
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
            createChannelPreview: {
              hideProfileImage: false,
              onlineStateText: false,
              statusMessage: false,
            },
            userListQuery: {
              limit: 20,
              metaDataKeyFilter: "name",
            },
            groupChannel: {
              enableCreate: true,
              enableLeave: true,
              channelListQuery: {
                includeEmpty: false,
                limit: 20,
                order: "latest_last_message",
              },
              createChannel: {
                enableSelectChannelType: false,
                channelType: "group",
              },
            },
          }}
        />
      </div>
    </div>
  );
};

export default Chat;
