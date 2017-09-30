package netty.work;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;

import netty.handler.FileUploadClientHandler;
import netty.util.FileUploadFile;
//文件上传客户端
public class FileUploadClient {
	public void connect(int port, String host,
			final FileUploadFile fileUploadFile) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline()
									.addLast(
											new ObjectDecoder(
													ClassResolvers
															.weakCachingConcurrentResolver(null)));
							ch.pipeline()
									.addLast(
											new FileUploadClientHandler(
													fileUploadFile));
						}
					});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
			System.out.println("FileUploadClient connect()结束");
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		int port = FILE_PORT;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUploadFile uploadFile = new FileUploadFile();
			File file = new File("d:/source.rar");// d:/source.rar,D:/2014work/apache-maven-3.5.0-bin.tar.gz
			String fileMd5 = file.getName();// 文件名
			uploadFile.setFile(file);
			uploadFile.setFile_md5(fileMd5);
			uploadFile.setStarPos(0);// 文件开始位置
			new FileUploadClient().connect(port, "127.0.0.1", uploadFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int FILE_PORT = 9991;
}
