//
//  CameraWebViewController.h
//
//  Created by Alex Wasner on 3/5/15.
//
//

#import <UIKit/UIKit.h>

@interface CameraWebViewController : UIViewController <UIWebViewDelegate>

@property (strong) UIImagePickerController* picker;
@property (strong) UIWebView* customWebView;

@end
