package com.findtech.threePomelos.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;

import com.findtech.threePomelos.utils.IContent;

/**
 * @author Administrator
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CubicBLEDevice extends BLEDevice {

	public CubicBLEDevice(Context context, BluetoothDevice bluetoothDevice) {

		super(context, bluetoothDevice);
	}

	@Override
	protected void discoverCharacteristicsFromService() {

	}

	/**
	 * 
	 * @param serviceUUID
	 * @param characteristicUUID
	 * @param
	 */
	public void writeValue(String serviceUUID, String characteristicUUID,
			byte[] value) {

		if (bleService == null) {
			return;
		}
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {
					IContent.getInstacne().WRITEVALUE = value;
					bluetoothGattCharacteristic.setValue(value);
					this.writeValue(bluetoothGattCharacteristic);
				}
			}
		}
	}
	/**
	 * @param serviceUUID
	 * @param characteristicUUID
	 * @param
	 */
	public void writeValue(String serviceUUID, String characteristicUUID, String value) {
		if (bleService == null) {
			return;
		}
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {

					int length = value.length();
					int lengthChar = 0;
					int position = 0;
					while (length > 0) {
						if (length > 20) {
							lengthChar = 20;
						} else if (length > 0) {
							lengthChar = length;
						} else {
							return;
						}
						String sendValue = value.substring(position, lengthChar+position);
						bluetoothGattCharacteristic.setValue(sendValue);
						bluetoothGattCharacteristic
								.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
						this.writeValue(bluetoothGattCharacteristic);
						
						length -= lengthChar;
						position += lengthChar;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param serviceUUID
	 * @param characteristicUUID
	 */
	public void readValue(String serviceUUID, String characteristicUUID) {
		if (bleService == null) {
			return;
		}
		for (BluetoothGattService bluetoothGattService : bleService
				.getSupportedGattServices(device)) {
			String gattServiceUUID = Long.toHexString(
					bluetoothGattService.getUuid().getMostSignificantBits())
					.substring(0, 4);
			for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService
					.getCharacteristics()) {
				String characterUUID = Long.toHexString(
						bluetoothGattCharacteristic.getUuid()
								.getMostSignificantBits()).substring(0, 4);
				if (gattServiceUUID.equals(serviceUUID)
						&& characteristicUUID.equals(characterUUID)) {
					this.readValue(bluetoothGattCharacteristic);
				}
			}
		}
	}


	@Override
	public void setNotification(
			boolean enable) {
		super.setNotification(enable);
	}

}
