//
//  NotificationDelegate.h
//  mac-utils
//
//  Created by Joey on 11/3/16.
//
//

#import <Cocoa/Cocoa.h>
#import <Foundation/Foundation.h>
#import "CBridge.h"

@interface NotificationDelegate : NSObject <NSUserNotificationCenterDelegate>
{
 @private
 NSMutableDictionary<NSNumber *, NSValue *> *callbacks;
}

- (NSNumber *) storeCallback:(void *) callback;

@end
