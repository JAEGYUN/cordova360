#import <UIKit/UIKit.h>

@class Gigaeyes360;

@interface PanoramaViewController : UIViewController {
    BOOL getFrame;
    //    videoRTSPlayer *video;
    float lastFrameTime;
}

-(void) imageTap;

@property (retain, nonatomic) IBOutlet UINavigationBar *navBar;
@property (retain, nonatomic) IBOutlet UIImageView *videoView;
@property (retain, nonatomic) Gigaeyes360* origem;
@property (retain, nonatomic) NSString* videoAddress;
@property (retain, nonatomic) NSString* playType;
@property (retain, nonatomic) NSString* camName;
@end
