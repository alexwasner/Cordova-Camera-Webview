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

bool lightStatusOn = false;

- (void) viewDidLoad
{
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillResignActive:) name:UIApplicationWillResignActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appWillTerminate:) name:UIApplicationWillTerminateNotification object:nil];
}

-(BOOL)prefersStatusBarHidden {
    return YES;
}

- (BOOL)shouldAutorotate
{
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone){
        return NO;
    }
    else{
        return YES;
    }
}
- (NSUInteger)supportedInterfaceOrientations {
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        return UIInterfaceOrientationMaskPortrait;
    }
    else {
        return UIInterfaceOrientationMaskAll;
    }
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
    self.customWebView.contentMode = UIViewContentModeScaleAspectFit;
    self.customWebView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    self.customWebView.scrollView.bounces = NO;
    self.customWebView.delegate = self;
    
    self.picker.cameraOverlayView = self.customWebView;
    
    //gesture to override autofocus
    self.touchAndHoldRecognizer = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(touchAndHold:)];
    self.touchAndHoldRecognizer.minimumPressDuration = 0.1;
    self.touchAndHoldRecognizer.allowableMovement = 600;
    [self.view addGestureRecognizer:self.touchAndHoldRecognizer];
    
    [self addChildViewController: self.picker];
    [self.picker didMoveToParentViewController: self];
    [self.view addSubview: self.picker.view];
    return self;
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    Class captureDeviceClass = NSClassFromString(@"AVCaptureDevice");
    if (captureDeviceClass != nil) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        if ([device hasTorch] && [device hasFlash]){
            [webView stringByEvaluatingJavaScriptFromString:@"document.getElementById(\"view-root\").className=\"has-flash\";"];
        }
    }
}

-(void)touchAndHold:(UILongPressGestureRecognizer *)theGesture{
    return;
}

-(BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType: (UIWebViewNavigationType)navigationType{
    NSString *requestURLString = [[request URL] absoluteString];
    if ([requestURLString hasPrefix:@"bridge:"]) {
        NSArray *components = [requestURLString componentsSeparatedByString:@":"];
        NSString *commandName = (NSString*)[components objectAtIndex:1];
        if ([commandName isEqualToString:@"toggleFlash"]) {
            [self toggleFlash];
        }else if ([commandName isEqualToString:@"close"]) {
            lightStatusOn = false;
            [[self presentingViewController] dismissViewControllerAnimated:YES completion:nil];
        }

        return NO;
    }
    return YES;
}

- (void)toggleFlash{
    Class captureDeviceClass = NSClassFromString(@"AVCaptureDevice");
    if (captureDeviceClass != nil) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        if ([device hasTorch] && [device hasFlash]){
            [device lockForConfiguration:nil];
            if (!lightStatusOn) {
                [device setTorchMode:AVCaptureTorchModeOn];
                [device setFlashMode:AVCaptureFlashModeOn];
                lightStatusOn = YES;
            } else {
                [device setTorchMode:AVCaptureTorchModeOff];
                [device setFlashMode:AVCaptureFlashModeOff];
                lightStatusOn = NO;
            }
            [device unlockForConfiguration];
        }
    }
}

-(void)appWillResignActive:(NSNotification*)note
{
    //execute pause on webview to stop flash
    if(self.customWebView != nil){
        [self.customWebView stringByEvaluatingJavaScriptFromString:@"var event = document.createEvent(\"HTMLEvents\");event.initEvent(\"pause\", true, true);document.dispatchEvent(event);"];
    }
    lightStatusOn = false;
    return;
}

-(void)appWillTerminate:(NSNotification*)note
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillResignActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillTerminateNotification object:nil];
    
}

@end