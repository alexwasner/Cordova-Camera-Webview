//
//  CameraViewController.m
//
//  Created by Alex Wasner on 3/5/15.
//
//

#import "CameraWebViewController.h"
#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>
#define CAMERA_SCALAR 1.6
@interface CameraWebViewController ()

@end

@implementation CameraWebViewController


- (void) viewDidLoad
{
    [super viewDidLoad];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



- ()init {
    self = [super initWithNibName:nil bundle:nil];
    self.picker = [[UIImagePickerController alloc] init];
    self.picker.sourceType = UIImagePickerControllerSourceTypeCamera;
    self.picker.cameraCaptureMode = UIImagePickerControllerCameraCaptureModePhoto;
    self.picker.cameraDevice = UIImagePickerControllerCameraDeviceRear;
    self.picker.showsCameraControls = NO;
    self.picker.navigationBarHidden = YES;
    self.picker.toolbarHidden = YES;
    self.picker.allowsEditing = false;
    if ([self.picker respondsToSelector:@selector(setEdgesForExtendedLayout:)]) {
        [self.picker setEdgesForExtendedLayout:UIRectEdgeNone];
    }
    self.picker.cameraViewTransform = CGAffineTransformScale(self.picker.cameraViewTransform, CAMERA_SCALAR, CAMERA_SCALAR);
    
    self.customWebView = [[UIWebView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.customWebView.userInteractionEnabled = YES;
    UIPinchGestureRecognizer *pinchRec = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:nil];
    [self.customWebView addGestureRecognizer:pinchRec];
    self.customWebView.opaque = NO;
    self.customWebView.backgroundColor = [UIColor clearColor];
    
    NSString *htmlFile = [[NSBundle mainBundle] pathForResource:@"www/webview" ofType:@"html"];
    NSString* htmlString = [NSString stringWithContentsOfFile:htmlFile encoding:NSUTF8StringEncoding error:nil];
    [self.customWebView loadHTMLString:htmlString baseURL:nil];
    
    self.picker.cameraOverlayView = self.customWebView;
    
    [self addChildViewController: self.picker];
    [self.picker didMoveToParentViewController: self];
    [self.view addSubview: self.picker.view];
    return self;
}

@end
