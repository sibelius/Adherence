package edu.arizona.adherence.sensormanager.sensors;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import zephyr.android.HxMBT.BTClient;
import zephyr.android.HxMBT.ConnectListenerImpl;
import zephyr.android.HxMBT.ConnectedEvent;
import zephyr.android.HxMBT.ZephyrPacketArgs;
import zephyr.android.HxMBT.ZephyrPacketEvent;
import zephyr.android.HxMBT.ZephyrPacketListener;
import zephyr.android.HxMBT.ZephyrProtocol;

public class NewConnectedListener extends ConnectListenerImpl
{
    public final static String BATTERY = "battery";
    public final static String HEARTRATE = "heartrate";
    public final static String HEARTBEATNUM = "heartbeatnum";
    public final static String HEARTBEATTS = "heartbeatts";
    public final static String DISTANCE = "distance";
    public final static String INSTANTSPEED = "instantspeed";
    public final static String STRIDES = "strides";

    public final static int DATA = 0x102;

	private Handler _aNewHandler;
	private int HR_SPD_DIST_PACKET =0x26;

	private final int HEART_RATE = 0x100;
	private final int INSTANT_SPEED = 0x101;
	private HRSpeedDistPacketInfo HRSpeedDistPacket = new HRSpeedDistPacketInfo();
	public NewConnectedListener(Handler handler, Handler _NewHandler) {
		super(handler, null);
		_aNewHandler = _NewHandler;
    }

	public void Connected(ConnectedEvent<BTClient> eventArgs) {
		System.out.println(String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));

		//Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms());
		//ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), );
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
				ZephyrPacketArgs msg = eventArgs.getPacket();
				byte CRCFailStatus;
				byte RcvdBytes;

				CRCFailStatus = msg.getCRCStatus();
				RcvdBytes = msg.getNumRvcdBytes() ;

                int batteryChargeInd;
                int heartRate; // Beats per minute
                int heartBeatNum;
                int[] heartBeatTS;
                double distance;
                double instantSpeed;
                int strides;

				if (HR_SPD_DIST_PACKET==msg.getMsgID())
				{
                    // Extract the data from the Packet and send to the Handler
                    byte [] DataArray = msg.getBytes();

                    batteryChargeInd = HRSpeedDistPacket.GetBatteryChargeInd(DataArray);
                    heartRate = HRSpeedDistPacket.GetHeartRate(DataArray);
                    heartBeatNum = HRSpeedDistPacket.GetHeartBeatNum(DataArray);
                    heartBeatTS = HRSpeedDistPacket.GetHeartBeatTS(DataArray);
                    distance = HRSpeedDistPacket.GetDistance(DataArray);
                    instantSpeed = HRSpeedDistPacket.GetInstantSpeed(DataArray);
                    strides = HRSpeedDistPacket.GetStrides(DataArray);

                    Message text = _aNewHandler.obtainMessage(DATA);
                    Bundle b = new Bundle();
                    b.putInt(BATTERY, batteryChargeInd);
                    b.putInt(HEARTRATE, heartRate);
                    b.putInt(HEARTBEATNUM, heartBeatNum);
                    b.putIntArray(HEARTBEATTS, heartBeatTS);
                    b.putDouble(DISTANCE, distance);
                    b.putDouble(INSTANTSPEED, instantSpeed);
                    b.putInt(STRIDES, strides);
                    text.setData(b);
                    _aNewHandler.sendMessage(text);
				}
			}
		});
	}
	
}