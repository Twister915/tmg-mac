//
//  PasteboardContentType.m
//  mac-utils
//
//  Created by Joey on 12/16/16.
//
//

#import "PasteboardContentType.h"

@implementation PasteboardContentType
+ (PasteboardContentType *)withName:(NSString *)name systemId:(NSString *)systemId {
    PasteboardContentType *returnVal = [[PasteboardContentType alloc] init];
    returnVal->internalId = name;
    returnVal->systemId = systemId;
    return returnVal;
}
@end
