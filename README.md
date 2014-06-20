# Pure Java ZigBee Application Framework
**Bekko** provides Java API for develop ZigBee application and allow user applications to communicate standard ZigBee devices.  
Unlike other "XBee API Java-Wrapper", Bekko features ZDO/ZDP implementations and standardized APS data connection API. You can implement ZCL application by using ZCL extension pacakge.

***Requirements***

* Java SE 1.4 or later / Java ME CDC 1.1 or later
* Supported ZigBee hardware

## Modules

* bekko-api: GCF compatible ZigBee API
* bekko-core: Hardware Abstraction Layer
* bekko-ember-shared: Common library for Ember (Silabs) device
* bekko-util: Common utility library
* bekko-xbee: XBee implementation
* bekko-zcl: **Full ZCL stack implementation**

### What is GCF ?
The Generic Connection Framework (JSR-30) is the Oracle standard networking API for both Java ME/SE.  
For more informations:  
<https://www.jcp.org/en/jsr/detail?id=30>

### What is ZCL ?
To communicate with the most commercial ZigBee standard products, you need to handle ZCL application protocol. Very similar to other modern low power wireless protocols, such as Bluetooh LE.  
Using ZCL is not mandatory for ZigBee specification though.

### What is XBee ?
XBee is the Digi International's RF module brand for embbeded use.  
For more informations:  
<http://www.digi.com/products/wireless-wired-embedded-solutions/zigbee-rf-modules/zigbee-mesh-module/>

## Supported Hardwares

### S2/S2B/S2C (ZigBee)
* XBee/XBee-PRO ZB
* XBee/XBee-PRO ZB SMT
* XBee/XBee-PRO SE *(S2 with SE Firmware)*

### S3B (DigiMesh)
* XBee-PRO 900HP  
***S3 and S3B with XSC firmware is not supported!***

### S8 (DigiMesh)
* XBee 865/868LP

## Implement for your own hardware
You can develop the implementation for your own devices!  
***bekko-core*** module provides common HAL for ZigBee standard devices. XBee implementation is fully developed top on the HAL.

## OK, show me some examples...
Coming soon!
