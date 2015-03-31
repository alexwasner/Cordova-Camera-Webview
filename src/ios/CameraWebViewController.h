//
//  CameraWebViewController.h
//
//  Created by Alex Wasner on 3/5/15.
//
//

#import <UIKit/UIKit.h>

@interface CameraWebViewController : UIViewController <UIWebViewDelegate, UIImagePickerControllerDelegate>

@property (strong) UIImagePickerController* picker;
@property (strong) UIWebView* customWebView;
@property (strong) UILongPressGestureRecognizer* touchAndHoldRecognizer;

@end
