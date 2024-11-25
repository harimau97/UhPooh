import React, { useEffect } from "react";
import SendbirdApp from "@sendbird/uikit-react/App";
import "@sendbird/uikit-react/dist/index.css";
import dayjs from "dayjs";
import "dayjs/locale/ko"; // Day.js의 한국어 로케일 추가
import withSendbird from "@sendbird/uikit-react/withSendbird";
import sendbirdSelectors from "@sendbird/uikit-react/sendbirdSelectors";

const Chat = (props) => {
  const sdk = sendbirdSelectors.getSdk(props);
  const currentUser = sdk && sdk.currentUser;
  const stringSet = {
    CHANNEL__MESSAGE_INPUT__PLACE_HOLDER: "메시지를 입력하세요.",
    CHANNEL__MESSAGE_INPUT__PLACE_HOLDER__DISABLED:
      "이 채널에서는 메시지를 보낼 수 없습니다.",
    CHANNEL__MESSAGE_LIST__NOTHING_TO_SHOW: "표시할 메시지가 없습니다.",
    CHANNEL__MESSAGE_LIST__LOADING: "메시지를 불러오는 중...",
    CHANNEL__HEADER__TITLE: "채팅",
    CHANNEL__HEADER__INFO: "채널 정보",
    CHANNEL__HEADER__LEAVE: "나가기",
    CHANNEL__MESSAGE_INPUT__SEND_BUTTON: "보내기",
    CHANNEL__FILE_ATTACHMENT__BUTTON: "파일 첨부",
    CHANNEL__MESSAGE_MENU__COPY: "복사",
    CHANNEL__MESSAGE_MENU__EDIT: "수정",
    CHANNEL__MESSAGE_MENU__DELETE: "삭제",
    CHANNEL__MESSAGE_MENU__RESEND: "다시 보내기",
    CHANNEL__MODERATION__MUTE: "음소거",
    CHANNEL__MODERATION__UNMUTE: "음소거 해제",
    CHANNEL__MODERATION__BAN: "추방",
    CHANNEL__MODERATION__UNBAN: "추방 해제",
    CHANNEL_LIST__TITLE: "채널 목록",
    CHANNEL_LIST__EMPTY: "참여 중인 채널이 없습니다.",
    CREATE_CHANNEL__TITLE: "새 채널 만들기",
    CREATE_CHANNEL__NAME_INPUT_PLACE_HOLDER: "채널 이름을 입력하세요.",
    CREATE_CHANNEL__USER_SELECTION_PLACE_HOLDER: "사용자를 선택하세요.",
    CREATE_CHANNEL__CREATE_BUTTON: "생성",
    SETTINGS__HEADER__TITLE: "채널 설정",
    SETTINGS__HEADER__CLOSE_BUTTON: "닫기",
    SETTINGS__MEMBERS__TITLE: "멤버",
    SETTINGS__MEMBERS__EMPTY: "채널 멤버가 없습니다.",
    SETTINGS__LEAVE_BUTTON: "채널 나가기",
    SETTINGS__DELETE_CHANNEL_BUTTON: "채널 삭제",
    SETTINGS__MODERATION__TITLE: "관리",
    SETTINGS__MODERATION__BANNED_USERS: "추방된 사용자",
    SETTINGS__MODERATION__MUTED_USERS: "음소거된 사용자",
    SETTINGS__MODERATION__BAN_USER: "사용자 추방",
    SETTINGS__MODERATION__MUTE_USER: "사용자 음소거",
    SETTINGS__MODERATION__UNBAN_USER: "추방 해제",
    SETTINGS__MODERATION__UNMUTE_USER: "음소거 해제",
    SETTINGS__MODERATION__CLOSE: "닫기",
    SETTINGS__GENERAL__TITLE: "일반 설정",
    SETTINGS__GENERAL__CHANNEL_NAME: "채널 이름",
    SETTINGS__GENERAL__CHANNEL_NAME_PLACE_HOLDER: "채널 이름을 입력하세요.",
    SETTINGS__GENERAL__SAVE_BUTTON: "저장",
    ERROR__GENERAL: "문제가 발생했습니다. 다시 시도해주세요.",
    ERROR__MESSAGE__FAILED_TO_LOAD: "메시지를 불러오지 못했습니다.",
    ERROR__CHANNEL__FAILED_TO_LOAD: "채널을 불러오지 못했습니다.",
  };

  // Disconnect function
  const disconnect = async () => {
    if (sdk) {
      try {
        await sdk.disconnect();
        console.log("Successfully disconnected from SendBird");
      } catch (error) {
        console.error("Error disconnecting from SendBird:", error);
      }
    }
  };

  // Expose disconnect function to parent
  if (props.onRef) {
    props.onRef({ disconnect });
  }

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

  useEffect(() => {
    const initializeChannel = async () => {
      if (sdk && props.config.USER_ID) {
        try {
          // Create user first
          const createUserUrl = `https://api-${props.config.APP_ID}.sendbird.com/v3/users`;
          const response = await fetch(createUserUrl, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              "Api-Token": props.config.API_TOKEN,
            },
            body: JSON.stringify({
              user_id: props.config.USER_ID,
              nickname: props.config.USER_EMAIL,
              profile_url: "",
            }),
          });

          if (!response.ok) {
            alert("User might already exist, proceeding with channel creation");
          }

          // Update user info
          await sdk.updateCurrentUserInfo({
            nickname: props.config.USER_EMAIL,
            userId: props.config.USER_ID,
          });

          // Create channel (기존 코드)
          const [channel] = await sdk.groupChannel.createChannel({
            invitedUserIds: [props.config.USER_ID],
            operatorUserIds: [props.config.USER_ID],
            name: "General Channel",
          });
          console.log("Channel initialized:", channel);
        } catch (error) {
          console.error("Error initializing channel:", error);
        }
      }
    };

    initializeChannel();
  }, [sdk, props.config.USER_ID]);

  // Sendbird UI Kit 컴포넌트 렌더링
  return (
    <div className="channel-wrap">
      <SendbirdApp
        appId={props.config.APP_ID}
        userId={props.config.USER_ID}
        breakpoint={true}
        nickname={props.config.USER_EMAIL}
        stringSet={stringSet}
        colorSet={{
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
        }}
        config={{
          isOnline: true,
          logLevel: "all",
          isReactionEnabled: true,
          showSearchIcon: true,
          showCreateChannel: true,
          showChannelSettings: true,
          showMessageReceipt: true,
          useMessageGrouping: true,
          allowProfileEdit: true,
          dateLocale: dayjs.locale("ko"),
        }}
        // channelListQuery={{
        //   limit: 15,
        //   includeEmpty: false,
        // }}
      />
    </div>
  );
};

// withSendbird로 컴포넌트 래핑
const ChatWithSendbird = withSendbird(Chat);
export default ChatWithSendbird;
