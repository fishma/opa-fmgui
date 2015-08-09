/**
* INTEL CONFIDENTIAL
* Copyright (c) 2014 Intel Corporation All Rights Reserved.
* The source code contained or described herein and all documents related to the source code ("Material")
* are owned by Intel Corporation or its suppliers or licensors. Title to the Material remains with Intel
* Corporation or its suppliers and licensors. The Material contains trade secrets and proprietary and
* confidential information of Intel or its suppliers and licensors. The Material is protected by
* worldwide copyright and trade secret laws and treaty provisions. No part of the Material may be used,
* copied, reproduced, modified, published, uploaded, posted, transmitted, distributed, or disclosed in
* any way without Intel's prior express written permission. No license under any patent, copyright,
* trade secret or other intellectual property right is granted to or conferred upon you by disclosure
* or delivery of the Materials, either expressly, by implication, inducement, estoppel or otherwise.
* Any license under such intellectual property rights must be express and approved by Intel in writing.
*/


/*******************************************************************************
 *                       I N T E L   C O R P O R A T I O N
 *	
 *  Functional Group: Fabric Viewer Application
 *
 *  File Name: PortDataAccumulator.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2014/06/02 19:58:54  rjtierne
 *  Archive Log:    Initial Version
 *  Archive Log:
 *
 *  Overview: Storage class for performance table accumulators
 *
 *  @author: rjtierne
 *
 ******************************************************************************/

package com.intel.stl.ui.monitor;

public class PortDataAccumulator {
    /**
     * Captured packet and data accumulators when querying totals data
     */
    private long rxCumulativePacket = 0;
    private long rxCumulativeData = 0;
    private long txCumulativePacket = 0;    
    private long txCumulativeData = 0;
    
    /**
     * Calculated packet and data accumulators when querying delta data
     */
    private long rxPacketAcc = 0;
    private long rxDataAcc = 0;
    private long txPacketAcc = 0;    
    private long txDataAcc = 0;
    /**
     * @return the rxCumulativePacket
     */
    public long getRxCumulativePacket() {
        return rxCumulativePacket;
    }
    /**
     * @param rxCumulativePacket the rxCumulativePacket to set
     */
    public void setRxCumulativePacket(long rxCumulativePacket) {
        this.rxCumulativePacket = rxCumulativePacket;
    }
    /**
     * @return the rxCumulativeData
     */
    public long getRxCumulativeData() {
        return rxCumulativeData;
    }
    /**
     * @param rxCumulativeData the rxCumulativeData to set
     */
    public void setRxCumulativeData(long rxCumulativeData) {
        this.rxCumulativeData = rxCumulativeData;
    }
    /**
     * @return the txCumulativePacket
     */
    public long getTxCumulativePacket() {
        return txCumulativePacket;
    }
    /**
     * @param txCumulativePacket the txCumulativePacket to set
     */
    public void setTxCumulativePacket(long txCumulativePacket) {
        this.txCumulativePacket = txCumulativePacket;
    }
    /**
     * @return the txCumulativeData
     */
    public long getTxCumulativeData() {
        return txCumulativeData;
    }
    /**
     * @param txCumulativeData the txCumulativeData to set
     */
    public void setTxCumulativeData(long txCumulativeData) {
        this.txCumulativeData = txCumulativeData;
    }
    /**
     * @return the rxPacketAcc
     */
    public long getRxPacketAcc() {
        return rxPacketAcc;
    }
    /**
     * @param rxPacketAcc - value by which to increment accumulator
     */
    public void incRxPacketAcc(long rxPacketAcc) {
        this.rxPacketAcc += rxPacketAcc;
        rxPacketAcc %= Long.MAX_VALUE;
    }
    /**
     * @return the rxDataAcc
     */
    public long getRxDataAcc() {
        return rxDataAcc;
    }
    /**
     * @param rxDataAcc - value by which to increment accumulator
     */
    public void incRxDataAcc(long rxDataAcc) {
        this.rxDataAcc += rxDataAcc;
        rxDataAcc %= Long.MAX_VALUE;
    }
    /**
     * @return the txPacketAcc
     */
    public long getTxPacketAcc() {
        return txPacketAcc;
    }
    /**
     * @param txPacketAcc - value by which to increment accumulator
     */
    public void incTxPacketAcc(long txPacketAcc) {
        this.txPacketAcc += txPacketAcc;
        txPacketAcc %= Long.MAX_VALUE;
    }
    /**
     * @return the txDataAcc
     */
    public long getTxDataAcc() {
        return txDataAcc;
    }
    /**
     * @param txDataAcc - value by which to increment accumulator
     */
    public void incTxDataAcc(long txDataAcc) {
        this.txDataAcc += txDataAcc;
        txDataAcc %= Long.MAX_VALUE;
    }
    
    @Override
    public String toString() {
        return "[rxCumulativePacket="+rxCumulativePacket + ", rxCumulativeData="+rxCumulativeData + 
              ", txCumulativePacket="+txCumulativePacket + ", txCumulativeData="+txCumulativeData +
              ", rxPacketAcc="+rxPacketAcc + ", rxDataAcc="+rxDataAcc +
              ", txPacketAcc="+txPacketAcc + ", txDataAcc="+txDataAcc +"]";
    }
       
    
    
    
        
    
    
        
    
    
}