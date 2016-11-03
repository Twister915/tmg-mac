#include "CBridge.h"

NSString *convertCString(const char *string);
NSPasteboard *getPasteboard();

NSPasteboard *getPasteboard() {
    return [NSPasteboard generalPasteboard];
}

const int sendNotification(const char *title, const char *subtitle, const char *message, const char *sound, notification_callback notify) {
    NSUserNotificationCenter *notificationCenter = [NSUserNotificationCenter defaultUserNotificationCenter];
    if (notificationCenter == nil)
        return -1;
    
    NSString *titleString = convertCString(title);
    NSString *subtitleString = convertCString(subtitle);
    NSString *messageString = convertCString(message);
    NSString *soundString = convertCString(sound);
    
    NSUserNotification *notification = [[NSUserNotification alloc] init];
    [notification setTitle:titleString];
    if (subtitle != NULL && subtitleString.length > 0)
        [notification setSubtitle:subtitleString];
    if (message != NULL && messageString.length > 0)
        [notification setInformativeText:messageString];
    [notification setSoundName:soundString];
    
    if ([notificationCenter delegate] == NULL) {
        [notificationCenter setDelegate:[[NotificationDelegate alloc] init]];
        NSLog(@"Allocating delegate...");
    }
    
    if (notify != NULL) {
        NotificationDelegate *delegate = (NotificationDelegate *)[notificationCenter delegate];
        NSNumber *identifier = [delegate storeCallback:(void *)notify];
        [notification setUserInfo:[NSDictionary dictionaryWithObjectsAndKeys:identifier, @"__id", nil]];
    }
    [notificationCenter deliverNotification:notification];
    return 0;
}

NSString *convertCString(const char *string) {
    if (string == NULL)
        return NULL;
    return [NSString stringWithCString:string encoding:NSUTF8StringEncoding];
}

const int writeToPasteboard(const char *data) {
    NSString *dataString = convertCString(data);
    if (dataString == NULL)
        return 0;
    
    const NSPasteboard *pasteboard = getPasteboard();
    NSArray<NSString *> *types = [[NSArray alloc] initWithObjects:NSStringPboardType, nil];
    [pasteboard declareTypes:types owner:nil];
    BOOL result = [pasteboard setString:dataString forType: NSStringPboardType];
    [types release];
    
    return result;
}

PasteboardContent **getPasteboardContents() {
    NSMutableArray<NSString *> *types = [[NSMutableArray alloc] initWithArray:[NSArray arrayWithObjects:NSFilenamesPboardType, NSURLPboardType, NSPasteboardTypeTIFF, NSPasteboardTypePNG, NSPasteboardTypePDF, NSHTMLPboardType, NSStringPboardType, nil]];
    
    NSMutableArray<NSString *> *resultTypes = [[NSMutableArray alloc] init];
    NSMutableArray<NSData *> *resultDatas = [[NSMutableArray alloc] init];
    
    NSPasteboard * _Nonnull pasteboard = getPasteboard();
    while ([types count] > 0) {
        NSString *aval = [pasteboard availableTypeFromArray:types];
        
        if (aval == NULL)
            break;
        
        [types removeObjectsInRange:NSMakeRange(0, [types indexOfObject:aval] + 1)];
        [resultTypes addObject:aval];
        [resultDatas addObject:[pasteboard dataForType:aval]];
    }
    
    long count = [resultTypes count];
    if (count == 0)
        return NULL;
    
    PasteboardContent **datas = malloc(sizeof(const PasteboardContent *) * (count + 1));
    
    NSEnumerator<NSData *> *dataEnum = [resultDatas objectEnumerator];
    NSEnumerator<NSString *> *typeEnum = [resultTypes objectEnumerator];
    for (long i = 0; i < count; i++) {
        NSData *bytes = [dataEnum nextObject];
        NSString *type = [typeEnum nextObject];
        PasteboardContent *data = malloc(sizeof(PasteboardContent));
        data->data = (unsigned char *)[bytes bytes];
        data->length = [bytes length];
        data->type = [type UTF8String];
        
        datas[i] = data;
    }
    datas[count] = NULL;
    
    [types release];
    [resultTypes release];
    [resultDatas release];
    return datas;
}


