/*
 * Author - Patricia Ramosova
 * Link - https://github.com/xramos00/DNS_client
 * Based on work of Martin Biolek (https://github.com/mbio16/clientDNS)
 * */
package models;


import lombok.Data;
import org.json.simple.JSONObject;

import enums.AUTHENTICATE_DATA;
import enums.AA;
import enums.CHECKING_DISABLED;
import enums.OP_CODE;
import enums.QR;
import enums.RA;
import enums.R_CODE;
import enums.RD;
import enums.TC;
import javafx.scene.control.TreeItem;

@Data
public class Header {
	UInt16 id;
	QR qr;
	OP_CODE opCode;
	AA aa;
	TC tc;
	RD rd;
	RA ra;
	CHECKING_DISABLED cd;
	AUTHENTICATE_DATA ad;
	R_CODE rCode;
	UInt16 QdCount;
	UInt16 AnCount;
	UInt16 NsCount;
	UInt16 ArCount;
	private TreeItem<String> root;
	private static final int size = 12;

	private static final String ID_KEY = "Id";
	static final String QR_KEY = "Message type";
	static final String OPCODE_KEY = "Opcode";
	static final String CONFLICT_KEY = "Conflict";
	private static final String AA_KEY = "Authoritative answer";
	static final String TC_KEY = "Truncation";
	static final String T_KEY = "Tentative";
	private static final String RD_KEY = "Recursion";
	private static final String CHECKING_DISABLED_KEY = "Checking disabled";
	private static final String AUTHENTICATE_DATA__KEY = "Authenticate data";
	static final String RCODE_KEY = "Response code";
	private static final String QDCOUNT_KEY = "Number of questions";
	private static final String ANCOUNT_KEY = "Number of answers";
	private static final String NSCOUNT_KEY = "Number of authority answers";
	private static final String ARCOUNT_KEY = "Number of additional records";

	public Header(boolean recursion, boolean dnssec, int numberOfQueries, boolean rrRecord) {
		id = new UInt16().generateRandom();
		root = new TreeItem<String>("Head");
		qr = QR.REQUEST;
		opCode = OP_CODE.QUERY;
		aa = AA.NON_AUTHORITATIVE;
		tc = TC.NON_TRUNCATED;
		rd = RD.getTypeByCode(recursion);
		ra = RA.RECURSION_NON_AVAIBLE;
		ad = AUTHENTICATE_DATA.getTypeByCode(dnssec);
		cd = CHECKING_DISABLED.getTypeByCode(!dnssec);
		rCode = R_CODE.NO_ERROR;
		QdCount = new UInt16(numberOfQueries);
		AnCount = new UInt16(0);
		NsCount = new UInt16(0);
		if (rrRecord) {
			ArCount = new UInt16(1);
		} else {
			ArCount = new UInt16(0);
		}

	}

	public Header (boolean recursion, boolean authenticateData, int numberOfQueries, boolean rrRecord, boolean checkingDisabled)
	{
		this(recursion,false,numberOfQueries,rrRecord);
		cd = CHECKING_DISABLED.getTypeByCode(checkingDisabled);
		ad = AUTHENTICATE_DATA.getTypeByCode(authenticateData);
	}

	public Header(int numberOfQueris) {
		this(false, false, numberOfQueris, true);
		this.id = new UInt16(0);
	}

	public Header() {
		root = new TreeItem<String>("Head");
	}

	public byte[] getHaderAsBytes() {
		boolean opcodeBoolean[] = DataTypesConverter.byteToBoolArr(opCode.code, 4);
		boolean sub1[] = { rd.code, tc.code, aa.code, opcodeBoolean[0], opcodeBoolean[1], opcodeBoolean[2],
				opcodeBoolean[3], qr.code };
		boolean rcodeBoolean[] = DataTypesConverter.byteToBoolArr(rCode.code, 4);
		boolean sub2[] = { rcodeBoolean[0], rcodeBoolean[1], rcodeBoolean[2], rcodeBoolean[3], cd.code, ad.code, false,
				ra.code };

		byte result[] = { id.getAsBytes()[1], id.getAsBytes()[0], DataTypesConverter.booleanArrayAsbyte(sub1),
				DataTypesConverter.booleanArrayAsbyte(sub2), QdCount.getAsBytes()[1], QdCount.getAsBytes()[0],
				AnCount.getAsBytes()[1], AnCount.getAsBytes()[0], NsCount.getAsBytes()[1], NsCount.getAsBytes()[0],
				ArCount.getAsBytes()[1], ArCount.getAsBytes()[0] };

		return result;
	}

	@Override
	public String toString() {
		return "Header [id=" + id.getValue() + ", qr=" + qr + ", opCode=" + opCode + ", aa=" + aa + ", tc=" + tc
				+ ", rd=" + rd + ", ra=" + ra + ", cd=" + cd + ", ad=" + ad + ", rCode=" + rCode + ", QdCount="
				+ QdCount.getValue() + ", AnCount=" + AnCount.getValue() + ", NsCount=" + NsCount.getValue()
				+ ", ArCount=" + ArCount.getValue() + "]";
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAsJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ID_KEY, id.getValue());
		jsonObject.put(QR_KEY, qr.code);
		jsonObject.put(OPCODE_KEY, opCode);
		if(aa != null){
			jsonObject.put(AA_KEY, aa.code);
		}
		jsonObject.put(TC_KEY, tc.code);
		if (rd != null) {
			jsonObject.put(RD_KEY, rd.code);
		}
		if (cd != null){
			jsonObject.put(CHECKING_DISABLED_KEY, cd.code);
		}
		if (ad != null){
			jsonObject.put(AUTHENTICATE_DATA__KEY, ad.code);
		}
		jsonObject.put(RCODE_KEY, rCode);
		jsonObject.put(QDCOUNT_KEY, QdCount.getValue());
		jsonObject.put(ANCOUNT_KEY, AnCount.getValue());
		jsonObject.put(NSCOUNT_KEY, NsCount.getValue());
		jsonObject.put(ARCOUNT_KEY, ArCount.getValue());
		return jsonObject;
	}

	public TreeItem<String> getAsTreeItem() {
		root.getChildren().add(new TreeItem<String>(ID_KEY + ": " + id.getValue()));
		root.getChildren().add(getFlagsAsTreeView());
		root.getChildren().add(new TreeItem<String>(QDCOUNT_KEY + ": " + QdCount.toString()));
		root.getChildren().add(new TreeItem<String>(ANCOUNT_KEY + ": " + AnCount.toString()));
		root.getChildren().add(new TreeItem<String>(NSCOUNT_KEY + ": " + NsCount.toString()));
		root.getChildren().add(new TreeItem<String>(ARCOUNT_KEY + ": " + ArCount.toString()));
		return root;
	}

	protected TreeItem<String> getFlagsAsTreeView() {
		String flagsKeys[] = { QR_KEY, OPCODE_KEY, AA_KEY, TC_KEY, RD_KEY, CHECKING_DISABLED_KEY,
				AUTHENTICATE_DATA__KEY, RCODE_KEY };
		String flagsValue[] = { qr.toString(), opCode.toString(), aa.toString(), tc.toString(), rd.toString(),
				cd.toString(), ad.toString(), rCode.toString() };
		TreeItem<String> main = new TreeItem<String>("Flags");
		for (int i = 0; i < flagsValue.length; i++) {
			main.getChildren().add(new TreeItem<String>(flagsKeys[i] + ": " + flagsValue[i]));
		}
		return main;
	}

	public Header parseHead(byte[] byteHead) {
		// id
		this.id = new UInt16().loadFromBytes(byteHead[0], byteHead[1]);

		// second byte - first in flags
		boolean[] pom1 = DataTypesConverter.byteToBoolArr(byteHead[2], 8);
		this.rd = RD.getTypeByCode(pom1[0]);
		this.tc = TC.getTypeByCode(pom1[1]);
		this.aa = AA.getTypeByCode(pom1[2]);
		boolean[] opcode = { pom1[3], pom1[4], pom1[5], pom1[6] };
		this.opCode = OP_CODE.getTypeByCode(DataTypesConverter.booleanArrayAsbyte(opcode));
		this.qr = QR.getTypeByCode(pom1[7]);

		// second byte in flags
		boolean[] pom2 = DataTypesConverter.byteToBoolArr(byteHead[3], 8);
		boolean[] rcodeBoolean = { pom2[0], pom2[1], pom2[2], pom2[3] };
		this.rCode = R_CODE.getTypeByCode(DataTypesConverter.booleanArrayAsbyte(rcodeBoolean));
		this.cd = CHECKING_DISABLED.getTypeByCode(pom2[4]);
		this.ad = AUTHENTICATE_DATA.getTypeByCode(pom2[5]);
		this.ra = RA.getTypeByCode(pom2[7]);

		this.QdCount = new UInt16().loadFromBytes(byteHead[4], byteHead[5]);
		this.AnCount = new UInt16().loadFromBytes(byteHead[6], byteHead[7]);
		this.NsCount = new UInt16().loadFromBytes(byteHead[8], byteHead[9]);
		this.ArCount = new UInt16().loadFromBytes(byteHead[10], byteHead[11]);
		return this;
	}

	public static int getSize() {
		return size;
	}

	public UInt16 getId() {
		return id;
	}

	public UInt16 getQdCount() {
		return QdCount;
	}

	public UInt16 getAnCount() {
		return AnCount;
	}

	public UInt16 getNsCount() {
		return NsCount;
	}

	public UInt16 getArCount() {
		return ArCount;
	}

}
