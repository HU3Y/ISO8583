package org.adelbs.iso8583.protocol;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;


public class ISOMessage {

	private int messageSize;
	private byte[] payload;

	private Bitmap bitmap;
	
	public ISOMessage(MessageVO messageVO) throws ParseException {
		this(null, messageVO);
	}
	
	/*
	private byte[] bytesTPDU5(String TPDU) {
		//formatar de 2 em 2 => 10 números = 5 bytes
		TPDU="6008180002";
		byte[] resultBytes = new byte[5];
		for(int i=0;i<5;i++) {
			resultBytes[i] = (byte) Integer.parseInt(TPDU.substring((i*2),(i*2)+2));
		}
		
		return resultBytes;
	}*/
	
	private byte[] bytesTPDU(String TPDU) {
		//formatar como 10 bytes
		byte[] resultBytes = new byte[10];
		for(int i=0;i<10;i++) {
			resultBytes[i] = (byte) Integer.parseInt(TPDU.substring(i,(i+1)));
		}
		
		return resultBytes;
	}
	
	
	public ISOMessage(byte[] payload, MessageVO messageVO) throws ParseException {
		
		if (payload != null)
			bitmap = new Bitmap(payload, messageVO);
		else
			bitmap = new Bitmap(messageVO);
		
        StringBuilder strMessage = new StringBuilder();
        this.payload = new byte[0];

        if (bitmap.getMessageVO().getHeader() != null) {
        	if (!bitmap.getMessageVO().getHeader().isEmpty()) {        	
        		strMessage.append(bitmap.getMessageVO().getHeader());
        		this.payload = ISOUtils.mergeArray(this.payload, bitmap.getMessageVO().getHeaderEncoding().convert(bitmap.getMessageVO().getHeader()));
        	}
        }
        if (!bitmap.getMessageVO().getTPDUValue().isEmpty()) {
            strMessage.append(bitmap.getMessageVO().getTPDUValue());
            this.payload = ISOUtils.mergeArray(this.payload, bytesTPDU(bitmap.getMessageVO().getTPDUValue()));
        }
        
		strMessage.append(messageVO.getType());
		strMessage.append(bitmap.getPayloadBitmap());
        
		this.payload = ISOUtils.mergeArray(this.payload, messageVO.getHeaderEncoding().convert(messageVO.getType()));
		//this.payload = ISOUtils.mergeArray(this.payload, EncodingEnum.EBCDIC.convert(messageVO.getType()));
		this.payload = ISOUtils.mergeArray(this.payload, bitmap.getPayloadBitmap());
		
		for (int i = 0; i <= bitmap.getSize(); i++) {
			if (bitmap.getBit(i) != null) {
				this.payload = ISOUtils.mergeArray(this.payload, bitmap.getBit(i).getPayloadValue());
				strMessage.append(bitmap.getBit(i).getPayloadValue());
			}
		}
		
		this.messageSize = strMessage.length();
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public FieldVO getBit(int bit) {
		return bitmap.getBit(bit);
	}
	
	public int getMessageSize() {
		return messageSize;
	}
	
	public String getMessageSize(int numChars) {
		String result = String.valueOf(messageSize);
		while (result.length() < numChars)
			result = "0" + result;
		return result;
	}
	
	public MessageVO getMessageVO() {
		return bitmap.getMessageVO();
	}
}
