package org.brewchain.bcapi.version;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.brewchain.bcapi.version.gens.Bcapi.PCommand;
import org.brewchain.bcapi.version.gens.Bcapi.PModule;
import org.brewchain.bcapi.version.gens.Bcapi.PRetGetVersion;
import org.brewchain.bcapi.version.gens.Bcapi.PRetGetVersion.PMBundleInfo;
import org.brewchain.bcapi.version.gens.Bcapi.PSGetVersion;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import onight.oapi.scala.commons.SessionModules;
import onight.osgi.annotation.NActorProvider;
import onight.tfw.async.CompleteHandler;
import onight.tfw.otransio.api.PacketHelper;
import onight.tfw.otransio.api.beans.FramePacket;

/**
 * 版本获取
 * 
 * @author brew
 *
 */
@NActorProvider
@Slf4j
@Data
public class GetVersion extends SessionModules<PSGetVersion> {

	@Override
	public String[] getCmds() {
		return new String[] { PCommand.VER.name() };
	}

	@Override
	public String getModule() {
		return PModule.BCA.name();
	}

	public long getFileSize(String location) {
		try {
			if (location.startsWith("file:/")) {
				File file = new File(location.substring("file:".length()));
				if (file.exists() && file.isFile()) {

					return file.length();
				}
			}
		} catch (Exception e) {
			log.error("cannot get file:" + location, e);
		}
		return 0;
	}

	String calcMD5(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte bb[] = str.getBytes();
			digest.update(bb, 0, bb.length);
			byte sha256[] = digest.digest();
			return Hex.encodeHexString(sha256);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // Can't happen.
		}
	}

	@Override
	public void onPBPacket(FramePacket pack, PSGetVersion pbo, CompleteHandler handler) {
		PRetGetVersion.Builder ret = PRetGetVersion.newBuilder();
		try {
			BundleContext context = BundleReference.class.cast(GetVersion.class.getClassLoader()).getBundle()
					.getBundleContext();
			String subversion = "";
			StringBuffer sb = new StringBuffer("BC_VERSION:");
			for (Bundle bundle : context.getBundles()) {
				sb.append(bundle.getBundleId() + "-" + bundle.getLocation() + "-" + bundle.getSymbolicName() + "-"
						+ bundle.getVersion() + "-" + getFileSize(bundle.getLocation()));
				subversion = subversion + bundle.getVersion();
				ret.addBundles(PMBundleInfo.newBuilder().setLocation(bundle.getLocation())
						.setFileSize("" + getFileSize(bundle.getLocation()))
						.setBundleId(String.valueOf(bundle.getBundleId()))
						.setLastModified("" + bundle.getLastModified()).setSymbolicName("" + bundle.getSymbolicName())
						.setVersion("" + bundle.getVersion().toString()));
			}
			subversion = ""+Math.abs(calcMD5(subversion).hashCode())%1000;
			ret.setRuntimeVersion("v1.3." + subversion + "-" + calcMD5(sb.toString()).substring(0, 8));
		} catch (Exception e) {
			ret.clear();
			log.error("Get Version Error:" + e.getMessage(), e);
		} finally {
			handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()));
		}

	}

	public static void main(String[] args) {
		System.out.println(Md5Crypt.md5Crypt("hello".getBytes()));
	}
}
