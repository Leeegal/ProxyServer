package Encryption;

import SecretCloudProxy.ReencryptionCipher;
import SecretCloudProxy.ReencryptionKey;
import SecretCloudProxy.ShareCipher;
import it.unisa.dia.gas.jpbc.Element;

public class ReencryptTask {
	//代理重加密
	//c'' = m * e(gA^r，g^s·xAt) * e(gA^-s·xA · H2^t (x)，g^rt) = shareCipher.cipher * e(rk.gH, shareCipher.grt)
	public static ReencryptionCipher reencryptMsg(encryptionModule module, ShareCipher shareCipher, ReencryptionKey rk) {
		Element m = module.newGTElementFromBytes(shareCipher.cipher).getImmutable();
		Element gH = module.newG1ElementFromBytes(rk.gH).getImmutable();
		Element grt = module.newG1ElementFromBytes(shareCipher.grt).getImmutable();
		Element egg = module.e(gH, grt).getImmutable();
		Element me = m.duplicate().mul(egg).duplicate().getImmutable();
		ReencryptionCipher cipher = new ReencryptionCipher(me.toBytes(), rk.CBx, rk.CBgrt2);
		return cipher;
	}
}
