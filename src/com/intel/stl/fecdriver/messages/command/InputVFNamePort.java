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
package com.intel.stl.fecdriver.messages.command;

/**
 * @author jijunwan
 * 
 */
public class InputVFNamePort extends InputLidPortNumber {
    private final String vfName;

    public InputVFNamePort(String vfName, int lid, byte portNumber) {
        this(vfName, lid, portNumber, false, 0, 0);
    }

    public InputVFNamePort(String vfName, int lid, byte portNumber,
            boolean delta) {
        this(vfName, lid, portNumber, delta, 0, 0);
    }

    public InputVFNamePort(String vfName, int lid, byte portNumber,
            long imageNumber, int imageOffset) {
        this(vfName, lid, portNumber, false, imageNumber, imageOffset);
    }

    public InputVFNamePort(String vfName, int lid, byte portNumber,
            boolean delta, long imageNumber, int imageOffset) {
        super(lid, portNumber, delta, imageNumber, imageOffset);
        this.vfName = vfName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.message.command.argument.InputImageId#getType()
     */
    @Override
    public InputType getType() {
        return InputType.InputTypeVFNamePort;
    }

    /**
     * @return the vfName
     */
    @Override
    public String getVfName() {
        return vfName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InputVFNamePort [vfName=" + vfName + ", getLid()=" + getLid()
                + ", getPortNumber()=" + getPortNumber() + ", getImageId()="
                + getImageId() + "]";
    }

}