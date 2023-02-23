package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private final Random randomEngine;
    private final ArrayList<Station> stations;

    private final double packetGenRate;
    int time, accumulatedDelay, sentPackages, availableTime, stationCount, queueSize, flag, packages_created;
    Station current_station;
    boolean flag_sent;


    /**
     * -INITIALIZATION-
     * every simulation has a number of stations
     * a prefixed queue size
     * and the packetGenRate as the load factor.
     * Every time we give a new load factor, we create the new simulation.
     */

    public Simulation(int stationCount, int queueSize, double packetGenRate ) {

        this.stations = new ArrayList<>();
        this.randomEngine = new Random();
        this.current_station = new Station(this, queueSize);

        this.stationCount = stationCount;
        this.queueSize = queueSize;
        this.packetGenRate = packetGenRate;

        this.time = 0;
        this.accumulatedDelay = 0;
        this.sentPackages = 0;
        this.packages_created = 0;
        this.flag = -1;

        this.flag_sent = false;

    }

    /**
     * @param iterations : the available slots
     */
    public void run(int iterations) {
        this.availableTime = iterations;

        initSimulation(); // creates the stations and their queues

        for (int i=0;i<iterations;i++)
            advanceSimulation(); // runs the actual simulation
    }

    /**
     * This method creates/adds the stations in a queue.
     * For every station we need: it's queue size and the information about the
     * simulation it's running for.
     */
    private void initSimulation() {
        for (var i = 0; i < stationCount; i++)
            this.stations.add(new Station(this, queueSize));
    }

    /**
     * This method is the core of the simulation. We check whether a station is able to transmit or not, and we act accordingly
     */
    public void advanceSimulation() {

        for(int i=0; i<stationCount;i+=2){
            flag_sent = false;

            populateStations(packetGenRate,i);  // for each station check if a packet comes
            double k = randomEngine.nextDouble();
            if (k < 0.5 && stations.get(i).hasPacket()) {  // then we check if the station has permission to transmit and if there´re any available packets
                flag_sent = true;
            }

            populateStations(packetGenRate,i+1);   // we check the same for the next station ( the station it shares a channel with)
            k = randomEngine.nextDouble();
            if (flag_sent) {  // the first wants to transmit
                if (k < 0.5 && stations.get(i+1).hasPacket()) {  // if both want to transmit, nothing happens - COLLISION
                    continue;
                }
                Station.Packet packet = stations.get(i).dequeuePacket();  // only the first can send
                if (packet==null)
                    continue;
                transmitPacket(packet);
            } else {  // the first can't transmit
                if (k < 0.5 && stations.get(i+1).hasPacket()) { // but the second can.
                    Station.Packet packet = stations.get(i+1).dequeuePacket();
                    if (packet==null)
                        continue;
                    transmitPacket(packet);
                }
            }
        }
        time++;  // the above happens simultaneously -in one slot- for all stations. that's why we increase the time var in the end
    }

    /**
     *  We check if the j station has a new packet
     * @param j: the station we want to populate
     */

    private void populateStations(double packetsPerStep,int j ) {

        if (randomEngine.nextDouble()<packetsPerStep) {
            var randomPayload = randomEngine.nextInt(256);
            boolean sent = this.stations.get(j).enqueuePacket(randomPayload);
            if (sent)
                packages_created++;
        }
    }

    /**
     * for every transmission we take into account the arrival time of the packet in the queue, and the time it left from it.
     */

    private void transmitPacket(Station.Packet p) {
        accumulatedDelay += (getTime() - p.arrivalTime);
        sentPackages += 1;
    }

    public int getTime() {return time;}

    /**
     * Throughput = sentPackages/ time(=slots)
     * Delay = accumulatedDelay / sentPackages
     * Load = packetGenRate
     * Loss = Packets_lost / packets_created ( for every station )
     */

    public double delay() {
        return accumulatedDelay / (double) sentPackages;
    }

    public double throughput() {
        return sentPackages/ (double) time ;
    }

    public double loss() {
        int sum = 0;
        for (int i=0;i<stationCount;i++){
            sum+=stations.get(i).getPackets_for_station();  // we find the loss from all the stations
        }
        if (packages_created==0)
            return 0.0;
        return  sum/(double)packages_created;}    // we divide the lost packets with the packets created to find the %loss of the simulation




}
