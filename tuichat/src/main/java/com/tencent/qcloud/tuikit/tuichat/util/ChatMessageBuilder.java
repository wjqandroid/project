package com.tencent.qcloud.tuikit.tuichat.util;

import android.net.Uri;

import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuicore.util.FileUtil;
import com.tencent.qcloud.tuicore.util.ImageUtil;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.message.FaceMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.FileMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.ImageMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.MergeMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.SoundMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TextAtMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TextMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.VideoMessageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageBuilder {

    public static TextMessageBean buildTextMessage(String message) {
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createTextMessage(message);

        TextMessageBean textMessageBean = new TextMessageBean();
        textMessageBean.setCommonAttribute(v2TIMMessage);
        textMessageBean.onProcessMessage(v2TIMMessage);
        textMessageBean.setExtra(textMessageBean.getText());
        return textMessageBean;
    }

    public static TextAtMessageBean buildTextAtMessage(List<String> atUserList, String message) {
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createTextAtMessage(message, atUserList);
        TextAtMessageBean textAtMessageBean = new TextAtMessageBean();
        textAtMessageBean.setCommonAttribute(v2TIMMessage);
        textAtMessageBean.onProcessMessage(v2TIMMessage);
        textAtMessageBean.setExtra(textAtMessageBean.getText());
        return textAtMessageBean;
    }

    /**
     * ????????????????????????????????????
     *
     * @param groupId  ?????????????????????????????????id
     * @param faceName ???????????????
     * @return
     */
    public static TUIMessageBean buildFaceMessage(int groupId, String faceName) {
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createFaceMessage(groupId, faceName.getBytes());
        FaceMessageBean message = new FaceMessageBean();
        message.setCommonAttribute(v2TIMMessage);
        message.onProcessMessage(v2TIMMessage);
        message.setExtra(TUIChatService.getAppContext().getString(R.string.custom_emoji));
        return message;
    }

    /**
     * ????????????????????????
     *
     * @param uri ?????? URI
     * @return
     */
    public static TUIMessageBean buildImageMessage(final Uri uri) {
        String path = ImageUtil.getImagePathAfterRotate(uri);
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createImageMessage(path);
        final ImageMessageBean messageBean = new ImageMessageBean();
        messageBean.setCommonAttribute(v2TIMMessage);
        messageBean.onProcessMessage(v2TIMMessage);

        messageBean.setDataUri(uri);
        int[] size = ImageUtil.getImageSize(path);
        messageBean.setDataPath(path);
        messageBean.setImgWidth(size[0]);
        messageBean.setImgHeight(size[1]);
        messageBean.setExtra(TUIChatService.getAppContext().getString(R.string.picture_extra));
        return messageBean;
    }

    /**
     * ????????????????????????
     *
     * @param imgPath   ?????????????????????
     * @param videoPath ????????????
     * @param width     ????????????
     * @param height    ????????????
     * @param duration  ???????????????
     * @return
     */
    public static TUIMessageBean buildVideoMessage(String imgPath, String videoPath, int width, int height, long duration) {
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createVideoMessage(videoPath, "mp4", (int) duration / 1000, imgPath);
        VideoMessageBean messageBean = new VideoMessageBean();
        messageBean.setCommonAttribute(v2TIMMessage);
        messageBean.onProcessMessage(v2TIMMessage);

        Uri videoUri = Uri.fromFile(new File(videoPath));
        messageBean.setImgWidth(width);
        messageBean.setImgHeight(height);
        messageBean.setDataPath(imgPath);
        messageBean.setDataUri(videoUri);
        messageBean.setExtra(TUIChatService.getAppContext().getString(R.string.video_extra));
        return messageBean;
    }

    /**
     * ????????????????????????
     *
     * @param recordPath ????????????
     * @param duration   ???????????????
     * @return
     */
    public static TUIMessageBean buildAudioMessage(String recordPath, int duration) {
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createSoundMessage(recordPath, duration / 1000);
        SoundMessageBean messageBean = new SoundMessageBean();
        messageBean.setCommonAttribute(v2TIMMessage);
        messageBean.onProcessMessage(v2TIMMessage);

        messageBean.setDataPath(recordPath);
        messageBean.setExtra(TUIChatService.getAppContext().getString(R.string.audio_extra));
        return messageBean;
    }

    /**
     * ????????????????????????
     *
     * @param fileUri ????????????
     * @return
     */
    public static TUIMessageBean buildFileMessage(Uri fileUri) {
        String filePath = FileUtil.getPathFromUri(fileUri);
        File file = new File(filePath);
        if (file.exists()) {
            V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createFileMessage(filePath, file.getName());
            FileMessageBean messageBean = new FileMessageBean();
            messageBean.setCommonAttribute(v2TIMMessage);
            messageBean.onProcessMessage(v2TIMMessage);

            messageBean.setDataPath(filePath);
            messageBean.setExtra(TUIChatService.getAppContext().getString(R.string.file_extra));
            return messageBean;
        }
        return null;
    }

    /**
     * ???????????? onebyone ????????????
     *
     * @param v2TIMMessage ??????????????????
     * @return
     */
    public static TUIMessageBean buildForwardMessage(V2TIMMessage v2TIMMessage) {
        V2TIMMessage forwardMessage = V2TIMManager.getMessageManager().createForwardMessage(v2TIMMessage);

        return buildMessage(forwardMessage);
    }

    /**
     * ???????????? merge ????????????
     *
     * @return
     */
    public static TUIMessageBean buildMergeMessage(List<TUIMessageBean> messageInfoList,
                                                   String title,
                                                   List<String> abstractList,
                                                   String compatibleText) {
        if (messageInfoList == null || messageInfoList.isEmpty()){
            return null;
        }
        List<V2TIMMessage> msgList = new ArrayList<>();
        for(int i = 0; i < messageInfoList.size(); i++){
            msgList.add(messageInfoList.get(i).getV2TIMMessage());
        }
        V2TIMMessage mergeMsg = V2TIMManager.getMessageManager()
                .createMergerMessage(msgList, title, abstractList, compatibleText);

        MergeMessageBean messageBean = new MergeMessageBean();
        messageBean.setCommonAttribute(mergeMsg);
        messageBean.onProcessMessage(mergeMsg);
        messageBean.setExtra(TUIChatService.getAppContext().getString(R.string.forward_extra));
        return messageBean;
    }

    /**
     * ???????????????????????????
     * @param data ?????????????????????????????????????????????
     * @param description ????????????????????????????????????????????????
     * @param extension ????????????
     * @return
     */
    public static TUIMessageBean buildCustomMessage(String data, String description, byte[] extension) {
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createCustomMessage(data.getBytes(), description, extension);
        TUIMessageBean message = ChatMessageParser.parseMessage(v2TIMMessage);
        if (message.getExtra() == null) {
            message.setExtra(TUIChatService.getAppContext().getString(R.string.custom_msg));
        }
        return message;
    }

    /**
     * ????????????????????????????????????
     *
     * @param customMessage ????????????
     * @return
     */
    public static V2TIMMessage buildGroupCustomMessage(String customMessage) {
        return V2TIMManager.getMessageManager().createCustomMessage(customMessage.getBytes());
    }

    public static TUIMessageBean buildMessage(V2TIMMessage message) {
        return ChatMessageParser.parseMessage(message);
    }

}
