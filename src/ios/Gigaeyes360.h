#import <Cordova/CDVPlugin.h>
#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import "PanoramaViewController.h"

@interface Gigaeyes360 : CDVPlugin

- (void) watchPanorama : (CDVInvokedUrlCommand*) command;
- (void) finishOkAndDismiss;

@property (strong,nonatomic) CDVInvokedUrlCommand* lastCommand;
@property (strong,nonatomic) PanoramaViewController* overlay;
@property (readwrite, assign) BOOL hasPendingOperation;

@end
