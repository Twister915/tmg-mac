#include "CBridge.h"

//grabs the pasteboard (util method)
NSPasteboard *getPasteboard() {
    return [NSPasteboard generalPasteboard];
}

//converts a const char * to NSString or keeps it as NULL if it is currently NULL
NSString *convertCString(const char *string) {
    if (string == NULL)
        return NULL;
    return [NSString stringWithCString:string encoding:NSUTF8StringEncoding];
}

//sends a notification (popup in corner) to the user immediately using the NSUserNotificationCenter
const int sendNotification(const char *title, const char *subtitle, const char *message, const char *sound, notification_callback notify) {
    NSUserNotificationCenter *notificationCenter = [NSUserNotificationCenter defaultUserNotificationCenter]; //grab notification center
    if (notificationCenter == nil) //this isn't going to be an issue I don't think
        return -1; //error
    
    NSString *titleString = convertCString(title); //convert ALL the strings
    NSString *subtitleString = convertCString(subtitle);
    NSString *messageString = convertCString(message);
    NSString *soundString = convertCString(sound);
    
    NSUserNotification *notification = [[NSUserNotification alloc] init]; //create a new notification
    [notification setTitle:titleString]; //start setting things
    if (subtitle != NULL && subtitleString.length > 0) //subtitle is optional
        [notification setSubtitle:subtitleString];
    if (message != NULL && messageString.length > 0) //and so is message
        [notification setInformativeText:messageString];
    [notification setSoundName:soundString]; //sound might be NULL but that's entirely ok since they accept NULL as a value

    if (notify != NULL) { //if we have a callback
        //we need to get the delegate from the notification center
        NotificationDelegate *delegate = (NotificationDelegate *) [notificationCenter delegate];
        
        if (delegate == NULL) { //and if it wasn't there when we checked, we need to init it
            delegate = [[NotificationDelegate alloc] init]; //so we do that
            [notificationCenter setDelegate:delegate]; //and set the notification center's delegate to this new instance
        }
        
        NSNumber *identifier = [delegate storeCallback:(void *)notify]; //then we store the callback that we got from java in the delegate's map, which gives us an identifier
        [notification setUserInfo:[NSDictionary dictionaryWithObjectsAndKeys:identifier, @"__id", nil]]; //the intent with the ID is to store it in the notification, so we oblige
    }
    [notificationCenter deliverNotification:notification]; //now that all the setup and stuff is done we can send the notification
    [notification release];
    return 0; //and return 0 (- numbers are errors, 0 is OK)
}

//lets us write some text to the pasteboard (clipboard)
const int writeToPasteboard(const char *data) {
    //convert the string for clipboard into NSString
    NSString *dataString = convertCString(data);
    if (dataString == NULL)
        return 0;
    
    //grab the pasteboard we're putting it into
    const NSPasteboard *pasteboard = getPasteboard();
    //create the NSStringPboardType, idk if this is needed, TODO test without this code in here (next two lines)
    NSArray<NSString *> *types = [[NSArray alloc] initWithObjects:NSStringPboardType, nil];
    [pasteboard declareTypes:types owner:nil];
    //set the text
    BOOL result = [pasteboard setString:dataString forType: NSStringPboardType];
    [types release]; //release the array we created
    
    return result; //and return the result (implicit cast from BOOL to const int)
}

PasteboardContent **getPasteboardContents() {
    //in order: the types of data that we want to support returning to the caller
    NSMutableArray<NSString *> *types = [[NSMutableArray alloc] initWithArray:[NSArray arrayWithObjects:NSFilenamesPboardType, NSURLPboardType, NSPasteboardTypeTIFF, NSPasteboardTypePNG, NSPasteboardTypePDF, NSHTMLPboardType, NSStringPboardType, nil]];
    //the resulting pasteboard data container (we will fill this up below)
    NSMutableDictionary<NSString *, NSData *> *resultData = [[NSMutableDictionary alloc] init];
    
    //we need to grab the pasteboard
    NSPasteboard * _Nonnull pasteboard = getPasteboard();
    while ([types count] > 0) { //and while we've not eliminated every type from the array
        //we should grab the first "type" that is in the pasteboard
        //(note this is in order, so everything preceeding it in the array is not a data type we're interested in
        //which means that we can say that if the object at index 5 is the object that comes out of this function, everything before it is not in the clipboard)
        NSString *aval = [pasteboard availableTypeFromArray:types];
        
        if (aval == NULL) //if nothing is found in the clipboard, that means no items remain which can be read, and this loop is to be broken
            break;
        
        //(otherwise) we remove all objects we have analyzed (everything preceeding and including the current type)
        [types removeObjectsInRange:NSMakeRange(0, [types indexOfObject:aval] + 1)];
        //and we get the data for the current type and put it in our mutable dictionary
        resultData[aval] = [pasteboard dataForType:aval];
    }
    [types release]; //release the types array now that we're done with it

    //now, with the count, we can transform it all into a format that's useful on the java side
    long count = [resultData count];
    if (count == 0) //return early because this would result in a useless pointer of null being created below (and then returned)
        return NULL;
    
    //start by mallocing enough space for a null terminated array of pointers to PasteboardContent
    PasteboardContent **datas = malloc(sizeof(const PasteboardContent *) * (count + 1));
    
    __block int i = 0; //then, with a block scoped int used to track the position in the datas array, we iterate through the dictionary
    [resultData enumerateKeysAndObjectsUsingBlock:^void(NSString *type, NSData *bytes, BOOL *stop) {
        //in which we create a PasteboardContent object and supply it with data derrived from the pasteboard content, including
        PasteboardContent *data = malloc(sizeof(PasteboardContent));
        data->data = (unsigned char *)[bytes bytes]; //the raw bytes in the clipboard
        data->length = [bytes length]; //the length of the bytes (since all java will have, to start off with, is a pointer)
        data->type = [type UTF8String]; //and the type from apple's pasteboard
        
        datas[i] = data; //then we assign this instance's pointer to the position i in the array above (PasteboardContent**)
        
        i++; //increment i, obviously
    }];
    datas[count] = NULL;
    
    return datas;
}