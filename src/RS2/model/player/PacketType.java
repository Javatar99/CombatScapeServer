package RS2.model.player;


	
public interface PacketType {
	void processPacket(Client c, int packetType, int packetSize);
}

