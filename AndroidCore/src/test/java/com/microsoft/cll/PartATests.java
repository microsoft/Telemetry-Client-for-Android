/**
 * Copyright Microsoft Corporation 2014
 * All Rights Reserved
 */
package com.microsoft.cll;

import com.microsoft.cll.Helpers.EventHelper;
import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Envelope;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test that we can fire events without any exceptions
 */
public class PartATests
{
    /**
     * Test setting of iKey
     */
    @Test
    public void testSettingIKey()
    {
        CustomPartA partA = new CustomPartA(new CustomLogger(), "testikey");
        Base event = (Base) EventHelper.generateBCEvent();
        try {
            Envelope envelope = partA.populate(event, null);
            assert(envelope.getIKey() == "testikey");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test setting of flags
     * 17 is the value we expect for an event with Normal Persistence and Normal Latency
     */
    @Test
    public void testSettingFlags()
    {
        CustomPartA partA = new CustomPartA(new CustomLogger(), "");
        Base event = (Base) EventHelper.generateBCEvent();
        try {
            Envelope envelope = partA.populate(event, null);
            assert (envelope.getFlags() == 17);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the sequence field
     */
    @Test
    public void testSequenceField() {
        CustomPartA partA = new CustomPartA(new CustomLogger(), "");
        Base event = (Base) EventHelper.generateBCEvent();
        String[] sequences = new String[25];

        try {
            for(int i = 0; i < 25; i++) {
                Envelope envelope = partA.populate(event, null);
                sequences[i] = envelope.getEpoch() + ":" + envelope.getSeqNum();
            }

            CheckForUniqueSession(sequences);
            CheckForMissingOrSkippedNumbers(sequences);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks to make sure all session id's are the same across all events
     * @param sequences All the events
     */
    private void CheckForUniqueSession(String[] sequences) {
        String session = sequences[0].split(":")[0];
        for(String sequence : sequences) {
            assert(sequence.split(":")[0].equals(session));
        }
    }

    /**
     * Checks to ensure that we have the correct number of elements in the list and that we aren't skipping numbers
     * @param sequences All the events
     */
    private void CheckForMissingOrSkippedNumbers(String[] sequences) {
        String session = sequences[0].split(":")[0];
        // Since other tests run before us they increment the seq counter so we have to
        // make sure we expect the seq counter to start above 1
        int first = Integer.parseInt(sequences[0].split(":")[1]);

        List<String> list = new ArrayList<String>();
        for(int i = first; i < first+25; i++) {
            list.add(session + ":" + i);
        }

        List<String> seqArray = new ArrayList<String>(Arrays.asList(sequences));
        seqArray.removeAll(list);
        assert(seqArray.size() == 0);
    }
}