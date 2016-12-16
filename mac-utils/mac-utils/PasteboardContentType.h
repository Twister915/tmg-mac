//
//  PasteboardContentType.h
//  mac-utils
//
//  Created by Joey on 12/16/16.
//
//

#import <Foundation/Foundation.h>

@interface PasteboardContentType : NSObject
{
    @public
    NSString *internalId, *systemId;
}

+ (PasteboardContentType *) withName:(NSString *)name systemId:(NSString *) systemId;
@end
