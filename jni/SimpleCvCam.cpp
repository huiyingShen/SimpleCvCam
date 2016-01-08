#include <jni.h>

#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

void getCanny(Mat rgb, Rect r);

extern "C" {

JNIEXPORT void JNICALL Java_org_ski_simplecvcam_SimpleCamAct_getCanny(JNIEnv*, jobject, jlong addrRgb)
{
	Mat &rgb = *(Mat *) addrRgb;
	int w = rgb.cols, h = rgb.rows;
	getCanny(rgb,Rect(w/4,h/4,w/2,h/2));
}

JNIEXPORT int JNICALL Java_org_ski_helloworld_MainActivity_test0(JNIEnv*, jobject, jlong addrData)
{
	Mat& mat = *(Mat*)addrData;
	mat.create(1,3,CV_32F);
	Mat_<float> &tmp = (Mat_<float> &)mat;
	Mat_<float>::iterator it;
	int i=1;
	for (it =tmp.begin(); it!= tmp.end(); it++,i++)
		*it = i;

	int sum = 0;
	for (it = tmp.begin(); it!= tmp.end(); it++)
		sum += *it;
	return sum;
}

}

void getCanny(Mat roi){
    Mat gray, edge;
    cvtColor(roi, gray, CV_RGB2GRAY);
    blur( gray, gray, Size(5,5));
    Canny( gray, edge, 50, 150, 3);
    edge.convertTo(gray, CV_8UC1);
    cvtColor(gray,roi, CV_GRAY2RGB);
}

void getCanny(Mat rgb, Rect r){
	Mat roi = rgb(r).clone();
	getCanny(roi);
	roi.copyTo(rgb(r));;
}
