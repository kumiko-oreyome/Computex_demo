package com.ncku.iir.computex;

import java.text.SimpleDateFormat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotFace;
import com.ncku.iir.computex.core.IRequest;
import com.ncku.iir.computex.sleep.SleepFragment;
import com.ncku.iir.computex.speech.Global;
import com.ncku.iir.computex.speech.SpeechFragment;
import com.ncku.iir.computex.speech.SpeechReaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class FaceDetection extends SpeechFragment implements View.OnClickListener,IRequest {
    /**
     * 相机状态:
     * 0: 预览
     * 1: 等待上锁(拍照片前将预览锁上保证图像不在变化)
     * 2: 等待预拍照(对焦, 曝光等操作)
     * 3: 等待非预拍照(闪光灯等操作)
     * 4: 已经获取照片
     */
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {

    }
    private SurfaceTexture mFakePreviewTexture;
    private Surface mFakePreviewSurface;

    //攝像頭ID (0代表後鏡頭 , 1代表前鏡頭)
    private String mCameraId = "0";
    //定義攝像頭
    private CameraDevice cameraDevice;

    private CameraCaptureSession captureSession;

    private ImageReader imageReader;

    /**
     * 当前的相机状态, 这里初始化为预览, 因为刚载入这个fragment时应显示预览
     */
    private int mState = STATE_PREVIEW;

    /**
     * 预览请求构建器, 用来构建"预览请求"(下面定义的)通过pipeline发送到Camera device
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * 预览请求, 由上面的构建器构建出来
     */
    private CaptureRequest mPreviewRequest;

    /**
     * 处理拍照等工作的子线程
     */
    private HandlerThread mBackgroundThread;

    /**
     * 上面定义的子线程的处理器
     */
    private Handler mBackgroundHandler;


    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
        private void speakWithFace(int milisec, String text){
            Global.api.robot.stopSpeak();
            Global.api.robot.setExpression(RobotFace.PLEASED);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Global.api.robot.setExpression(RobotFace.HIDEFACE);
                }
            }, milisec);
            Global.api.robot.speak(text) ;

        }
        // 自定义的一个处理方法
        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // 状态是预览时, 不需要做任何事情
                    Integer mode = result.get(CaptureResult.STATISTICS_FACE_DETECT_MODE);
                    Face[] faces = result.get(CaptureResult.STATISTICS_FACES);
                    Log.e("tag","face :  " + faces.length);
                    if(faces.length>=1) {
                        if(cameraDevice!=null){
                            cameraDevice.close();
                        }
                        Log.e("tag","face :  " + faces.length);
                        SimpleDateFormat sdf=new SimpleDateFormat("HH");
                        int hour= Integer.valueOf(sdf.format(new java.util.Date())).intValue();
                        if(hour<12){
                            speakWithFace(7000,"早安");
                        }
                        else if(hour>=12 && hour<18){
                            speakWithFace(7000,"午安");
                        }
                        else if(hour>=18 ){
                            speakWithFace(7000,"晚安");
                        }
                        jumpNextFragment(new SleepFragment());
                        //lockFocus();
                    }
                    break;
                }
                case STATE_WAITING_LOCK: {
                    // 等待锁定的状态, 某些设备完成锁定后CONTROL_AF_STATE可能为null
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // 如果焦点已经锁定(不管自动对焦是否成功), 检查AE的返回, 注意某些设备CONTROL_AE_STATE可
                        // 能为空
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            // 如果自动曝光(AE)设定良好, 将状态置为已经拍照, 执行拍照
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // 等待预处理状态, 某些设备CONTROL_AE_STATE可能为null
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        // 如果AE需要做于拍照或者需要闪光灯, 将状态置为"非等待预拍照"(翻译得有点勉强)
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // 某些设备CONTROL_AE_STATE可能为null
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        // 如果AE做完"非等待预拍照", 将状态置为已经拍照, 并执行拍照操作
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        //攝像頭被打開時觸發
        @Override
        public void onOpened(CameraDevice camera) {
            FaceDetection.this.cameraDevice = camera;
            //開始拍照
            createCameraCaptureSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
            FaceDetection.this.cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            FaceDetection.this.cameraDevice = null;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Global.api.robot.speak("photo");
        Global.api.robot.setExpression(RobotFace.DEFAULT);
        startBackgroundThread();
        openCamera(1920,1080);
    }

    @Override
    public void onClick(View view) {
        //captureStillPicture();
        lockFocus();
    }

    /**
     * 开启子线程
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


    /**
     * 锁定焦点(拍照的第一步)
     */
    private void lockFocus() {
        try {
            // 构建自动对焦请求
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // 告诉mCaptureCallback回调状态
            mState = STATE_WAITING_LOCK;
            // 提交一个捕获单一图片的请求个相机
            captureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解开锁定的焦点
     */
    private void unlockFocus() {
        try {
            // 构建失能AF的请求
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            // 构建自动闪光请求(之前拍照前会构建为需要或者不需要闪光灯, 这里重新设回自动)
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 提交以上构建的请求
            captureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // 拍完照后, 设置成预览状态, 并重复预览请求
            mState = STATE_PREVIEW;
            captureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }





    private void captureStillPicture() {
        try {
            if (cameraDevice == null) {
                return;
            }

            //創建作為拍照的CaptureRequest.Builder
            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            // 将imageReader的surface作为CaptureRequest.Builder的目標
            captureRequestBuilder.addTarget(imageReader.getSurface());
            //設置自動對焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //設置自動曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 獲取設備方向
            int rotation = Global.ma.getWindowManager().getDefaultDisplay().getRotation();
            // 計算照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            // 停止連續拍照
            captureSession.stopRepeating();

            // 獲取靜態圖像
            captureSession.capture(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                // 拍照完成時觸發
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    unlockFocus();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    // 打開鏡頭
    private void openCamera(int width, int height) {

        setUpCameraOutputs(width, height);
        CameraManager manager = (CameraManager) Global.ma.getSystemService(Context.CAMERA_SERVICE);
        try {
            // 打開鏡頭
            if (ActivityCompat.checkSelfPermission(Global.ma, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Global.ma, new String[]{Manifest.permission.CAMERA}, 0);
            }
            manager.openCamera(mCameraId, stateCallback, null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutputs(int width, int height){
        CameraManager manager = (CameraManager) Global.ma.getSystemService(Context.CAMERA_SERVICE);
        try{
            // 獲取指定鏡頭的特性
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);

            // 獲取鏡頭支持的配置
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // 獲取鏡頭支持的最大尺寸
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
            // 創建ImageReader，獲得圖像數據
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),ImageFormat.JPEG, 2);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Context getContext() {
        return Global.ma;
    }

    @Override
    public void onGetMessage(String text) {

    }

    @Override
    public SpeechReaction getSpeechReaction() {
        return new SpeechReaction() {
            @Override
            public void onSentenceEnd(String text) {
//                Global.api.robot.speak("人臉偵測");
            }
        };
    }


    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs)
        {

            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private void createCameraCaptureSession(){
        try
        {
            mFakePreviewTexture = new SurfaceTexture(10); // 10: magic number
            mFakePreviewTexture.setDefaultBufferSize(1920, 1080);
            mFakePreviewSurface = new Surface(mFakePreviewTexture);

            mPreviewRequestBuilder
                    = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mFakePreviewSurface);

            cameraDevice.createCaptureSession(Arrays.asList(mFakePreviewSurface, imageReader.getSurface()), new CameraCaptureSession.StateCallback()
                    {
                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession)
                        {

                            if (null == cameraDevice)
                            {
                                return;
                            }

                            captureSession = cameraCaptureSession;

                            // 自动对焦
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                            mPreviewRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
                                    CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL);

                            // 自动闪光
                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                            // 构建上述的请求
                            mPreviewRequest = mPreviewRequestBuilder.build();
                            // 重复进行上面构建的请求, 以便显示预览
                            try {
                                captureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
                        {
                            Toast.makeText(Global.ma, "配置失败！", Toast.LENGTH_SHORT).show();
                        }
                    }, null
            );
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}
