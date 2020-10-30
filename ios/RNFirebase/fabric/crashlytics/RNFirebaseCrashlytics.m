#import "RNFirebaseCrashlytics.h"
#import <Firebase/Firebase.h>

@implementation RNFirebaseCrashlytics
    RCT_EXPORT_MODULE();

    RCT_EXPORT_METHOD(crash) {
        @throw NSInternalInconsistencyException;
    }

    RCT_EXPORT_METHOD(log:(NSString *)message) {
        [[FIRCrashlytics crashlytics] log:message];
    }

   RCT_EXPORT_METHOD(recordError:
    (NSDictionary *) jsErrorDict) {
        [self recordJavaScriptError:jsErrorDict];
    }

   RCT_EXPORT_METHOD(recordCustomError:
    (NSDictionary *) jsErrorDict) {
        [self recordJavaScriptError:jsErrorDict];
    }

    RCT_EXPORT_METHOD(setBoolValue:(NSString *)key boolValue:(NSString *)boolValue) {
        [[FIRCrashlytics crashlytics] setCustomValue:boolValue forKey:key];
    }

    RCT_EXPORT_METHOD(setFloatValue:(NSString *)key floatValue:(nonnull NSNumber *)floatValue) {
        [[FIRCrashlytics crashlytics] setCustomValue:floatValue forKey:key];

    }

    RCT_EXPORT_METHOD(setIntValue:(NSString *)key intValue:(nonnull NSNumber *)intValue) {
        [[FIRCrashlytics crashlytics] setCustomValue:intValue forKey:key];
    }

    RCT_EXPORT_METHOD(setStringValue:(NSString *)key stringValue:(NSString *)stringValue) {
        [[FIRCrashlytics crashlytics] setCustomValue:stringValue forKey:key];
    }

    RCT_EXPORT_METHOD(setUserIdentifier:(NSString *)userId) {
        [[FIRCrashlytics crashlytics] setUserID:userId];
    }

    RCT_EXPORT_METHOD(setUserName:(NSString *)userName)
    {
        [[FIRCrashlytics crashlytics] setUserID:userName];
    }

    RCT_EXPORT_METHOD(setUserEmail:(NSString *)email)
    {
        [[FIRCrashlytics crashlytics] setUserID:email];
    }

    RCT_EXPORT_METHOD(enableCrashlyticsCollection) {
        [FIRApp configure];
    }


    - (void)recordJavaScriptError:(NSDictionary *)jsErrorDict {
        NSString *message = jsErrorDict[@"message"];
        NSDictionary *stackFrames = jsErrorDict[@"frames"];
        NSMutableArray *stackTrace = [[NSMutableArray alloc] init];
        BOOL isUnhandledPromiseRejection = [jsErrorDict[@"isUnhandledRejection"] boolValue];

        for (NSDictionary *stackFrame in stackFrames) {
            FIRStackFrame *customFrame = [FIRStackFrame stackFrameWithSymbol:stackFrame[@"fn"] file:stackFrame[@"file"] line:(uint32_t) [stackFrame[@"line"] intValue]];
            [stackTrace addObject:customFrame];
        }

        NSString *name = @"JavaScriptError";
        if (isUnhandledPromiseRejection) {
            name = @"UnhandledPromiseRejection";
        }

        FIRExceptionModel *exceptionModel = [FIRExceptionModel exceptionModelWithName:name reason:message];
        exceptionModel.stackTrace = stackTrace;

        [[FIRCrashlytics crashlytics] recordExceptionModel:exceptionModel];
    }

@end
