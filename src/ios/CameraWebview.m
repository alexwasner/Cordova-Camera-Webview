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
        
//
//    
////    __weak CameraWebview* weakSelf = self;
////    
////    [self.commandDelegate runInBackground:^{
////        BOOL hasCamera = [UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera];
////        if (!hasCamera) {
////            NSLog(@"Camera.getPicture: source type %lu not available.", (unsigned long)UIImagePickerControllerSourceTypeCamera);
////            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No camera available"];
////            [weakSelf.commandDelegate sendPluginResult:result callbackId:command.callbackId];
////            return;
////        }
////        
////        // If a popover is already open, close it; we only want one at a time.
////        if (([[weakSelf pickerController] pickerPopoverController] != nil) && [[[weakSelf pickerController] pickerPopoverController] isPopoverVisible]) {
////            [[[weakSelf pickerController] pickerPopoverController] dismissPopoverAnimated:YES];
////            [[[weakSelf pickerController] pickerPopoverController] setDelegate:nil];
////            [[weakSelf pickerController] setPickerPopoverController:nil];
////        }
////        
////        UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
////        imagePicker.delegate = self;
////        imagePicker.sourceType =  UIImagePickerControllerSourceTypeCamera;
////        
////        objPopView = [[UIPopoverController alloc] initWithContentViewController:imagePicker];
////        [self presentViewController:imagePicker animated:YES completion:nil];
//    
////        [self presentViewController:aviary animated:YES completion:nil];
////        CDVCamera* cameraPicker = [CDVCamera createFromPictureOptions:pictureOptions];
////        weakSelf.pickerController = cameraPicker;
////        
////        cameraPicker.delegate = weakSelf;
////        cameraPicker.callbackId = command.callbackId;
////        // we need to capture this state for memory warnings that dealloc this object
////        cameraPicker.webView = weakSelf.webView;
////
////        if ([weakSelf popoverSupported] && (pictureOptions.sourceType != UIImagePickerControllerSourceTypeCamera)) {
////            if (cameraPicker.pickerPopoverController == nil) {
////                cameraPicker.pickerPopoverController = [[NSClassFromString(@"UIPopoverController") alloc] initWithContentViewController:cameraPicker];
////            }
////            [weakSelf displayPopover:pictureOptions.popoverOptions];
////            weakSelf.hasPendingOperation = NO;
////            
////        } else {
////            dispatch_async(dispatch_get_main_queue(), ^{
////                [weakSelf.viewController presentViewController:cameraPicker animated:YES completion:^{
////                    weakSelf.hasPendingOperation = NO;
////                }];
////            });
////        }
//    }];
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