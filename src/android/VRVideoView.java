package kr.co.anylogic.gigaeyes360;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;

import android.widget.Toast;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import java.util.ArrayList;

public class VRVideoView extends GLSurfaceView  implements IVLCVout.Callback {
    private static String TAG = "VRVideoView";
    private VideoRender mRenderer;
    private kr.co.anylogic.gigaeyes360.Camera      mCamera;

    private LibVLC libvlc;

    private MediaPlayer mMediaPlayer = null;
    private File file = null;
    private String filePath = null;
    private Uri uri = null;
    private String title = "";
    public VRVideoView(Context context, File file) {
        super(context);
        this.file = file;
        init();
    }

    public VRVideoView(Context context, String filePath) {
        super(context);
        this.filePath = filePath;
        init();
    }

    public VRVideoView(Context context, Uri uri) {
        super(context);
        this.uri = uri;
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);

        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        mRenderer = new VideoRender(getContext());
        setRenderer(mRenderer);

        mRenderer.set360VRMode(true);

        mCamera = new Camera();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onSurfacesCreated(IVLCVout ivlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout ivlcVout) {

    }

    @Override //VLC 레이아웃 설정
    public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

    }

    @Override  //하드웨어 가속 에러시 플레이어 종료
    public void onHardwareAccelerationError(IVLCVout vout) {
        releasePlayer();
        Toast.makeText(this.getContext(), "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }

    //VLC 플레이어 실행
    private void createPlayer(SurfaceTexture surfaceTexture) {
        releasePlayer();
        try {

            // Create LibVLC
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
//            options.add("--aout=opensles");
            options.add("--rtsp-tcp"); // time stretching
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(options);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoSurface(surfaceTexture);
            vout.addCallback(this);
            vout.attachViews();

            Media m = null;
            if (file != null) {
                m = new Media(libvlc, file.getAbsolutePath());
            } else if (filePath != null) {
                m = new Media(libvlc, filePath);
            } else if (uri != null) {
                m = new Media(libvlc, uri);
            }

            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();

        } catch (Exception e) {
            Log.e(TAG,"Error creating player!" );
            e.printStackTrace();
        }
    }

    //플레이어 종료
    private void releasePlayer() {
        Log.d(TAG, "player release!!!");
        if (libvlc == null)
            return;
        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        libvlc.release();
        libvlc = null;

    }

    public void setTouchEvent(MotionEvent event){
        if(mRenderer == null && mCamera == null)
            return;

        float posx = event.getX();
        float posy = event.getY();

        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            mCamera.setMotionPos(posx, posy);
        }

        mCamera.mLastX = posx;
        mCamera.mLastY = posy;
    }


    private class VideoRender implements GLSurfaceView.Renderer,
            SurfaceTexture.OnFrameAvailableListener {
        private String TAG = "VideoRender";

        private static final int FLOAT_SIZE_BYTES = 4;
        private static final int SHORT_SIZE_BYTES = 2;
        private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 3 * FLOAT_SIZE_BYTES;
        private static final int TEXTURE_VERTICES_DATA_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES;
        private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 0;

        private final float[] mPlaneVerticesData = {
                -1.0f, -1.0f, 0,
                1.0f, -1.0f, 0,
                -1.0f, 1.0f, 0,
                1.0f, 1.0f, 0, };

        private final float[] mPlaneTextureData = {
                0.f, 0.0f,
                1.0f, 0.f,
                0.0f, 1.f,
                1.0f, 1.0f };

        private FloatBuffer mPlaneVertices;
        private FloatBuffer mPlaneTextures;

        private FloatBuffer mSphereVertices;
        private FloatBuffer mSphereTextures;
        private ShortBuffer mIndexBuffer;

        private final String mVertexShader = "uniform mat4 uMVPMatrix;\n"
                + "uniform mat4 uSTMatrix;\n" + "attribute vec4 aPosition;\n"
                + "attribute vec4 aTextureCoord;\n"
                + "varying vec2 vTextureCoord;\n" + "void main() {\n"
                + "  gl_Position = uMVPMatrix * aPosition;\n"
                + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" + "}\n";

        private final String mFragmentShader = "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "varying vec2 vTextureCoord;\n"
                + "uniform samplerExternalOES sTexture;\n"
                + "void main() {\n"
                + "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"
                + "}\n";

        private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];
        private float[] mProjMatrix = new float[16];
        private float[] mMMatrix = new float[16];
        private float[] mVMatrix = new float[16];

        private int mProgram;
        private int mTextureID;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle;
        private int maTextureHandle;

        private SurfaceTexture mSurface;

        private int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

        // indices buffer
        short[] mIndices;
        boolean m360VRMode;

        private float mRatio;
        private float mFOV = 60.0f;
        private float mNear = 0.1f;
        private float mFar = 10.0f;

        private VideoRender(Context context) {

            // create sphere buffer
            CreateSphereBuffer(1.0f, 40, 40);

            // create plabe buffer
            mPlaneVertices = ByteBuffer
                    .allocateDirect(
                            mPlaneVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mPlaneVertices.put(mPlaneVerticesData).position(0);

            // extra
            mPlaneTextures = ByteBuffer
                    .allocateDirect(
                            mPlaneTextureData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mPlaneTextures.put(mPlaneTextureData).position(0);

            // make sphere buffer
            mSphereVertices = ByteBuffer

                    .allocateDirect(
                            Sphere.mVertices.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mSphereVertices.put(Sphere.mVertices).position(0);

            mSphereTextures = ByteBuffer
                    .allocateDirect(
                            Sphere.mUV.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mSphereTextures.put(Sphere.mUV).position(0);

            mIndexBuffer = ByteBuffer
                    .allocateDirect(
                            mIndices.length * SHORT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            mIndexBuffer.put(mIndices);
            mIndexBuffer.position(0);

            Matrix.setIdentityM(mSTMatrix, 0);

        }

        private void set360VRMode(boolean flag){
            m360VRMode = flag;
        }

        private void CreateSphereBuffer(float fRadius, int iRings, int iSectors){

            int r, s;
            int size_index_indices = 0;

            mIndices = new short[(iRings - 1) * (iSectors - 1) * 6];

            for (r = 0; r < iRings - 1; r++)
            {
                for (s = 0; s < iSectors - 1; s++)
                {
                    mIndices[size_index_indices++] = (short) (r * iSectors + s);       //(a)
                    mIndices[size_index_indices++] = (short) (r * iSectors + (s + 1));    //(b)
                    mIndices[size_index_indices++] = (short) ((r + 1) * iSectors + (s + 1));  // (c)

                    mIndices[size_index_indices++] = (short) (r * iSectors + s);       //(a)
                    mIndices[size_index_indices++] = (short) ((r + 1) * iSectors + (s + 1));  // (c)
                    mIndices[size_index_indices++] = (short) ((r + 1) * iSectors + s);     //(d)
                }
            }
        }

        public void onDrawFrame(GL10 glUnused) {

            // clear buffer
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
                    | GLES20.GL_COLOR_BUFFER_BIT);

            // some functions
            mSurface.updateTexImage();
            mSurface.getTransformMatrix(mSTMatrix);

            if(m360VRMode)
            {
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                GLES20.glDepthFunc(GLES20.GL_LESS);
            }

            // use shader program
            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            // bind texture id
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

            if(m360VRMode)
            {

                mSphereVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
                GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                        false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
                        mSphereVertices);

                checkGlError("glVertexAttribPointer maPosition");
                GLES20.glEnableVertexAttribArray(maPositionHandle);
                checkGlError("glEnableVertexAttribArray maPositionHandle");

                mSphereTextures.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
                GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT,
                        false, TEXTURE_VERTICES_DATA_STRIDE_BYTES, mSphereTextures);
            }
            else
            {
                mPlaneVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
                GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                        false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
                        mPlaneVertices);
                checkGlError("glVertexAttribPointer maPosition");
                GLES20.glEnableVertexAttribArray(maPositionHandle);
                checkGlError("glEnableVertexAttribArray maPositionHandle");

                mPlaneTextures.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
                GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT,
                        false, TEXTURE_VERTICES_DATA_STRIDE_BYTES, mPlaneTextures);
            }

            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");

            if(m360VRMode && mCamera != null)
            {
                // make identity matrix
                Matrix.setIdentityM(mMVPMatrix, 0);
                Matrix.setIdentityM(mMMatrix, 0);

                mCamera.computeLookAtMatrix();

                // camera navigation
                Matrix.multiplyMM(mMVPMatrix, 0, mCamera.getViewMatrix(), 0, mMMatrix, 0);
                Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

                GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix,
                        0);

                GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

                // render sphere
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexBuffer.limit(),
                        GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

            }
            else
            {
                Matrix.setIdentityM(mMVPMatrix, 0);

                GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix,
                        0);
                GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                checkGlError("glDrawArrays");
            }

            GLES20.glFinish();

        }

        public void onSurfaceChanged(GL10 glUnused, int width, int height) {

            mRatio = (float) width / height;

            GLES20.glViewport(0, 0, width, height);

            Matrix.perspectiveM(mProjMatrix, 0, mFOV, mRatio, mNear, mFar);

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mProgram = createProgram(mVertexShader, mFragmentShader);
            if (mProgram == 0) {
                return;
            }
            maPositionHandle = GLES20
                    .glGetAttribLocation(mProgram, "aPosition");
            checkGlError("glGetAttribLocation aPosition");
            if (maPositionHandle == -1) {
                throw new RuntimeException(
                        "Could not get attrib location for aPosition");
            }
            maTextureHandle = GLES20.glGetAttribLocation(mProgram,
                    "aTextureCoord");
            checkGlError("glGetAttribLocation aTextureCoord");
            if (maTextureHandle == -1) {
                throw new RuntimeException(
                        "Could not get attrib location for aTextureCoord");
            }

            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,
                    "uMVPMatrix");
            checkGlError("glGetUniformLocation uMVPMatrix");
            if (muMVPMatrixHandle == -1) {
                throw new RuntimeException(
                        "Could not get attrib location for uMVPMatrix");
            }

            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram,
                    "uSTMatrix");
            checkGlError("glGetUniformLocation uSTMatrix");
            if (muSTMatrixHandle == -1) {
                throw new RuntimeException(
                        "Could not get attrib location for uSTMatrix");
            }

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            mTextureID = textures[0];
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
            checkGlError("glBindTexture mTextureID");

            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            mSurface = new SurfaceTexture(mTextureID);
            mSurface.setOnFrameAvailableListener(this);

            createPlayer(mSurface);
        }

        synchronized public void onFrameAvailable(SurfaceTexture surface) {

        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            if (shader != 0) {
                GLES20.glShaderSource(shader, source);
                GLES20.glCompileShader(shader);
                int[] compiled = new int[1];
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,
                        compiled, 0);
                if (compiled[0] == 0) {
                    Log.e(TAG, "Could not compile shader " + shaderType + ":");
                    Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                    GLES20.glDeleteShader(shader);
                    shader = 0;
                }
            }
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                    fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            int program = GLES20.glCreateProgram();
            if (program != 0) {
                GLES20.glAttachShader(program, vertexShader);
                checkGlError("glAttachShader");
                GLES20.glAttachShader(program, pixelShader);
                checkGlError("glAttachShader");
                GLES20.glLinkProgram(program);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,
                        linkStatus, 0);
                if (linkStatus[0] != GLES20.GL_TRUE) {
                    Log.e(TAG, "Could not link program: ");
                    Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteProgram(program);
                    program = 0;
                }
            }
            return program;
        }

        private void checkGlError(String op) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e(TAG, op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }

    }

}


