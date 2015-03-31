//
//  CameraWebview.m
//
//  Created by Alex Wasner on 7/15/2014.
//  Copyright 2014 Alex Wasner. All rights reserved.
//

#import "CameraWebview.h"
#import "MainViewController.h"
#import "CameraWebViewController.h"

@implementation CameraWebview

- (void) start:(CDVInvokedUrlCommand*)command
{
    
    if (![UIImagePickerController isCameraDeviceAvailable:UIImagePickerControllerCameraDeviceRear]) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No rear camera detected"];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    } else if (![UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Camera is not accessible"];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    } else {
        CameraWebViewController *cameraViewController = [[CameraWebViewController alloc] init];
        [self.viewController presentViewController:cameraViewController animated:YES completion:nil];
    }
}

- (void) stop:(CDVInvokedUrlCommand*)command
{
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MainViewController *rootViewController = (MainViewController*) window.rootViewController;
    [rootViewController setRotationAllowed:YES];
    [self.webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"window.plugins.orientationLock.success('%s');", "success"]];
}

@end