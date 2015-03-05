//
//  CameraViewController.m
//
//  Created by Alex Wasner on 3/5/15.
//
//

#import "CameraWebViewController.h"
#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>
#define CAMERA_SCALAR 1.7
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
    
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone){
        self.picker.cameraViewTransform = CGAffineTransformScale(self.picker.cameraViewTransform, CAMERA_SCALAR, CAMERA_SCALAR);
    }
    
    
    self.customWebView = [[UIWebView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.customWebView.userInteractionEnabled = YES;
    UIPinchGestureRecognizer *pinchRec = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:nil];
    [self.customWebView addGestureRecognizer:pinchRec];
    self.customWebView.opaque = NO;
    self.customWebView.backgroundColor = [UIColor clearColor];
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:@"webview" ofType:@"html" inDirectory:@"www"]];
    [self.customWebView loadRequest:[NSURLRequest requestWithURL:url]];
    
    self.customWebView.scalesPageToFit = YES;
    self.picker.cameraOverlayView = self.customWebView;
    self.customWebView.contentMode = UIViewContentModeScaleAspectFit;
    self.customWebView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    self.customWebView.scrollView.bounces = NO;
    
    [self addChildViewController: self.picker];
    [self.picker didMoveToParentViewController: self];
    [self.view addSubview: self.picker.view];
    return self;
}
@end