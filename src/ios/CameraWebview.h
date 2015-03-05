//
//  CameraWebview.h
//
//  Created by Alex Wasner on 7/15/2014.
//  Copyright 2014 Alex Wasner. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>


@interface CDVCamera : UIImagePickerController

@property (copy)   NSString* callbackId;
@property (strong) UIPopoverController* pickerPopoverController;
@property (strong) UIView* webView;


@end
@interface CameraWebview : CDVPlugin <UIImagePickerControllerDelegate,
                                      UINavigationControllerDelegate,
                                      UIPopoverControllerDelegate>
{}
@property (strong) UIImagePickerController* picker;

- (void) start:(CDVInvokedUrlCommand*) command;
- (void) stop:(CDVInvokedUrlCommand*) command;

@end
