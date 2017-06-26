package kr.co.anylogic.gigaeyes360;


import android.opengl.Matrix;

public class Camera {

    private float[] mVMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    private float[] mPosition = new float[3];
    private float[] mDirection = new float[3];
    private float[] mRight = new float[3];
    private float[] mUp = new float[3];

    public float mHorizontalAngle = 3.14f;
    public float mVerticalAngle = 0.0f;

    public float mLastX = 0.0f;
    public float mLastY = 0.0f;

    public Camera(){

        // camera position
        mPosition[0] = 0.0f;
        mPosition[1] = 0.0f;
        mPosition[2] = 0.0f;

        // direction vector
        mDirection[0] = (float) (Math.cos(mVerticalAngle) * Math.sin(mHorizontalAngle));
        mDirection[1] = (float)(Math.sin(mVerticalAngle));
        mDirection[2] = (float)(Math.cos(mVerticalAngle) * Math.cos(mHorizontalAngle));

        // up vector
        mUp[0] = 0.0f;
        mUp[1] = 1.0f;
        mUp[2] = 0.0f;

        // look at matrix initialized
        Matrix.setLookAtM(mVMatrix, 0,
                mPosition[0], mPosition[1], mPosition[2],
                mDirection[0], mDirection[1], mDirection[2],
                mUp[0], mUp[1], mUp[2]);

    }

    // cross calculation
    public void cross(float[] p1, float[] p2, float[] result){
        result[0] = p1[1] * p2[2] - p2[1] * p1[2];
        result[1] = p1[2] * p2[0] - p2[2] * p1[0];
        result[2] = p1[0] * p2[1] - p2[0] * p1[1];
    }

    public void setMotionPos(float posx, float posy){

        // compute delta angles
        mHorizontalAngle += (posx - mLastX) * 0.001f;
        mVerticalAngle += (posy - mLastY) * 0.001f;

    }

    // compute lookat matrix
    public void computeLookAtMatrix(){

        // make direction vector
        mDirection[0] = (float) (Math.cos(mVerticalAngle) * Math.sin(mHorizontalAngle));
        mDirection[1] = (float)(Math.sin(mVerticalAngle));
        mDirection[2] = (float)(Math.cos(mVerticalAngle) * Math.cos(mHorizontalAngle));

        // make right vector
        mRight[0] = (float) Math.sin(mHorizontalAngle - 3.14f/2.0f);
        mRight[1] = 0.0f;
        mRight[2] = (float) Math.cos(mHorizontalAngle - 3.14f/2.0f);

        // make up vector
        cross(mRight, mDirection, mUp);

        // make lookat matrix
        Matrix.setLookAtM(mVMatrix, 0,
                mPosition[0], mPosition[1], mPosition[2],
                mDirection[0], mDirection[1], mDirection[2],
                mUp[0], mUp[1], mUp[2]);
    }

    public float[] getViewMatrix(){
        return mVMatrix;
    }

    public void setProjectionMatrix(float[] p){

        System.arraycopy(p, 0, mProjectionMatrix, 0, p.length);
    }

    public float[] getProjectionMatrix(){
        return mProjectionMatrix;
    }

}
