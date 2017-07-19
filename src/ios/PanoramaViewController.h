#import <UIKit/UIKit.h>

@class Gigaeyes360;

@interface PanoramaViewController : UIViewController {
    BOOL getFrame;
    float lastFrameTime;
}

-(void) imageTap;

@property (retain, nonatomic) Gigaeyes360* origem;
@property (retain, nonatomic) NSString* videoAddress;
@property (retain, nonatomic) NSString* playType;
@property (retain, nonatomic) NSString* camName;
@end
