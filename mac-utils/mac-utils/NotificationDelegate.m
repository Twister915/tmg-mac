//
//  NotificationDelegate.m
//  mac-utils
//
//  Created by Joey on 11/3/16.
//
//

#import "NotificationDelegate.h"

@implementation NotificationDelegate
- (id)init {
    callbacks = [[NSMutableDictionary alloc] init];
    return self;
}

- (void) dealloc {
    [callbacks release];
    [super dealloc];
}

- (void)userNotificationCenter:(NSUserNotificationCenter *)center didDeliverNotification:(NSUserNotification *)notification {
    
}

- (void)userNotificationCenter:(NSUserNotificationCenter *)center didActivateNotification:(NSUserNotification *)notification {
    NSNumber *identifier = (NSNumber *)([notification userInfo][@"__id"]);
    NSValue *callbackVal = callbacks[identifier];
    if (callbackVal == NULL)
        return;
    
    notification_callback cb = [callbackVal pointerValue];
    
    if (cb == NULL)
        return;
    
    cb();
}

- (BOOL)userNotificationCenter:(NSUserNotificationCenter *)center shouldPresentNotification:(NSUserNotification *)notification {
    return YES;
}

- (NSNumber *) storeCallback:(void *) callback {
    NSNumber *randomIdentifier = [NSNumber numberWithLong:arc4random_uniform(10000)];
    [callbacks setObject:[NSValue valueWithPointer:callback] forKey:randomIdentifier];
    return randomIdentifier;
}
@end
