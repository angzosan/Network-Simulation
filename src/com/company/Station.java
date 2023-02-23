package com.company;

import java.util.LinkedList;
import java.util.Random;

public class Station {
    private final LinkedList<Packet> queue;
    private final Simulation simulation;
    private final int maxSize;

    int packets_for_station; //how many packets we tried to give to the station

    static class Packet {
        int payload;
        int arrivalTime;

        public Packet(int payload, int arrivalTime) {
            this.payload = payload;
            this.arrivalTime = arrivalTime;
        }
    }

    public Station(Simulation simulation, int maxSize) {
        this.queue = new LinkedList<>();
        this.simulation = simulation;
        this.maxSize = maxSize;
    }


    /**
     * adds a new packet at the beginning of the queue
     * @param payload (currently not used)
     */
    public boolean enqueuePacket(int payload) {

        if (queue.size() == maxSize) {   // if we've reached the max amount of packets in the queue, we lose the packet
            packets_for_station++; // we keep the number of packets that gets lost
            return false;
        }
        queue.add(new Packet(payload, simulation.getTime()));   // otherwise, we add it in
        return true;

    }

    /**
     * removes first element (every new packet is placed in the beginning of the list)
     * @return the transmitted packet
     */
    public Packet dequeuePacket() {
        return queue.pollFirst();
    }

    boolean hasPacket() {
        return !queue.isEmpty();
    }

    public int getPackets_for_station() { return packets_for_station; }

}
