package org.ski.simplecvcam;

import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class SimpleCamAct extends Activity implements SurfaceHolder.Callback{
	int width = 960, height=720;
	Camera mCamera;
	MyView myView;

	Bitmap bmp2Display = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
	
	Mat yuv,bgr;
	boolean isCvLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LinearLayout theLayoyt = (LinearLayout) findViewById(R.id.theLayout);
		myView = new MyView(this);
		theLayoyt.addView(myView, width, height);
		SurfaceHolder holder = myView.getHolder();
		holder.setFixedSize(width, height);
		holder.addCallback(this);
		myView.setWillNotDraw(false);

		
		mLoaderCallback = new BaseLoaderCallback(this) {
			@Override
			public void onManagerConnected(int status) {
				switch (status) {
				case LoaderCallbackInterface.SUCCESS:
					yuv = new Mat(height + height/2, width, CvType.CV_8UC1);
					bgr = new Mat(height, width, CvType.CV_8UC3);
                    Log.i("SimpleCamAct: ", "OpenCV loaded successfully");	
                    System.loadLibrary("SimpleCvCam");
                    isCvLoaded = true;
					break;
				default:
					super.onManagerConnected(status);
				}
			}

		};
	}


	private BaseLoaderCallback  mLoaderCallback;
	@Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
	
	Camera.PreviewCallback cbWithBuffer = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] _data, Camera _camera) {
			if (isCvLoaded){
				yuv.put(0, 0, _data);
				Imgproc.cvtColor(yuv, bgr, Imgproc.COLOR_YUV2RGBA_NV21, 3);
				getCanny(bgr.getNativeObjAddr());
				Utils.matToBitmap(bgr,bmp2Display);
				myView.bmp2Display = bmp2Display;
				myView.invalidate();
			}
			_camera.addCallbackBuffer(_data);
		}
	};
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		mCamera.addCallbackBuffer(new byte[width*height*3/2]); // YUV
		mCamera.setPreviewCallbackWithBuffer(cbWithBuffer);		
		
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
			// TODO: add more exception handling logic here
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	    Camera.Parameters parameters = mCamera.getParameters();
	    parameters.setPreviewSize(width, height);
	    mCamera.setParameters(parameters);
	    mCamera.setPreviewCallbackWithBuffer(cbWithBuffer);
	    mCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.release();
		mCamera = null;
		
	}

	native public void getCanny(long matAddrBgr);
}

class MyView extends SurfaceView{
	Bitmap bmp2Display;
	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	Paint paint = new Paint();
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (bmp2Display!=null){
			canvas.drawBitmap(bmp2Display, 0, 0, paint);
		}
		canvas.drawText("time = "+System.currentTimeMillis(), 50, 100, paint);

	}
	
	
}

