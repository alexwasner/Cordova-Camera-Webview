//
//  CameraWebview.h
//
//  Created by Alex Wasner on 7/15/2014.
//  Copyright 2014 Alex Wasner. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>


@interface CameraWebview : CDVPlugin {
    
}

- (void) start:(CDVInvokedUrlCommand*) command;
- (void) stop:(CDVInvokedUrlCommand*) command;

@end
