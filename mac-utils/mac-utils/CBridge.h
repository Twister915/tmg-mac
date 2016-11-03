#ifndef CBridge_h
#define CBridge_h
#import "NotificationDelegate.h"
#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    const char *type;
    unsigned char *data;
    unsigned long length;
} PasteboardContent;
    
typedef void(*notification_callback)(void);
    
const int sendNotification(const char *title, const char *subtitle, const char *message, const char *sound, notification_callback callback);
PasteboardContent **getPasteboardContents();
const int writeToPasteboard(const char *data);
    
#ifdef __cplusplus
}
#endif

#endif /* CBridge_h */
