package com.microsoft.cll.android;

import com.microsoft.cll.android.Helpers.EventHelper;
import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Envelope;
import com.microsoft.telemetry.extensions.device;
import com.microsoft.telemetry.extensions.os;
import com.microsoft.telemetry.extensions.user;

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
        CustomPartA partA = new CustomPartA(new CustomLogger(), "testikey", new CorrelationVector());
        Base event = (Base) EventHelper.generateBCEvent();
        try {
            Envelope envelope = partA.populateEnvelope(event, null, 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
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
        CustomPartA partA = new CustomPartA(new CustomLogger(), "", new CorrelationVector());
        Base event = (Base) EventHelper.generateBCEvent();

        Envelope envelope = partA.populateEnvelope(event, null, 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert (envelope.getFlags() == 0x101);
        envelope = partA.populateEnvelope(event, "cv", 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.REALTIME);
        assert (envelope.getFlags() == 0x201);
        envelope = partA.populateEnvelope(event, "cv", 0, Cll.EventPersistence.CRITICAL, Cll.EventLatency.REALTIME);
        assert (envelope.getFlags() == 0x202);
        envelope = partA.populateEnvelope(event, "cv", 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, EventSensitivity.Mark);
        assert (envelope.getFlags() == 0x80101);
        envelope = partA.populateEnvelope(event, "cv", 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, EventSensitivity.Hash);
        assert (envelope.getFlags() == 0x100101);
        envelope = partA.populateEnvelope(event, "cv", 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, EventSensitivity.Mark, EventSensitivity.Drop);
        assert (envelope.getFlags() == 0x280101);
    }

    /**
     * Tests the sequence field
     */
    @Test
    public void testSequenceField() {
        CustomPartA partA = new CustomPartA(new CustomLogger(), "", new CorrelationVector());
        Base event = (Base) EventHelper.generateBCEvent();
        String[] sequences = new String[25];

        try {
            for(int i = 0; i < 25; i++) {
                Envelope envelope = partA.populateEnvelope(event, null, 0, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
                sequences[i] = envelope.getEpoch() + ":" + envelope.getSeqNum();
            }

            CheckForUniqueSession(sequences);
            CheckForMissingOrSkippedNumbers(sequences);
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testCS20Population() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Init();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();

        com.microsoft.telemetry.cs2.Envelope envelope = partA.populateLegacyEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, null);
        assert(envelope.getVer() == 1);
        assert (!envelope.getName().isEmpty());
        assert (!envelope.getTime().isEmpty());
        assert (envelope.getSampleRate() == 10);
        assert (!envelope.getSeq().isEmpty());
        assert (!envelope.getIKey().isEmpty());
        assert (envelope.getFlags() == 0x101);
        assert (envelope.getTags() != null);
        assert (envelope.getTags().get("cV").equals(correlationVector.GetValue()));
        assert (!envelope.getDeviceId().isEmpty());
        assert (!envelope.getOs().isEmpty());
        assert (!envelope.getOsVer().isEmpty());
        assert (!envelope.getAppId().isEmpty());
        assert (!envelope.getAppVer().isEmpty());
        assert (!envelope.getUserId().isEmpty());
    }

    @Test
    public void testCS21Populaation() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Init();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(envelope.getVer().equals("2.1"));
        assert (!envelope.getName().isEmpty());
        assert (!envelope.getTime().isEmpty());
        assert (envelope.getPopSample() == 10);
        assert (envelope.getSeqNum() != 0);
        assert (!envelope.getIKey().isEmpty());
        assert (envelope.getFlags() == 0x101);
        assert (envelope.getCV().equals(correlationVector.GetValue()));
        assert (!envelope.getEpoch().isEmpty());
        assert (!envelope.getOs().isEmpty());
        assert (!envelope.getOsVer().isEmpty());
        assert (!envelope.getAppId().isEmpty());
        assert (!envelope.getAppVer().isEmpty());
        assert (!((device)envelope.getExt().get("device")).getLocalId().isEmpty());
        assert (!((user)envelope.getExt().get("user")).getLocalId().isEmpty());
    }

    @Test
    public void testHashPII() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Init();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelopeUnHashed = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, EventSensitivity.Hash);
        assert(envelope.getVer().equals("2.1"));
        assert (envelope.getName().equals(envelopeUnHashed.getName()));
        assert (!envelope.getTime().isEmpty());
        assert (envelope.getPopSample() == 10);
        assert (envelope.getSeqNum() != 0);
        assert (envelope.getIKey().equals(envelopeUnHashed.getIKey()));
        assert (partA.HashStringSha256(envelopeUnHashed.getCV()).equals(envelope.getCV()));
        assert (partA.HashStringSha256(envelopeUnHashed.getEpoch()).equals(envelope.getEpoch()));
        assert (envelope.getFlags() == 0x100101);
        assert (!envelope.getOs().isEmpty());
        assert (!envelope.getOsVer().isEmpty());
        assert (!envelope.getAppId().isEmpty());
        assert (!envelope.getAppVer().isEmpty());
        assert ("d:".concat(partA.HashStringSha256(((device)envelopeUnHashed.getExt().get("device")).getLocalId())).equals(((device) envelope.getExt().get("device")).getLocalId()));
        assert ("d:".concat(partA.HashStringSha256(((user)envelopeUnHashed.getExt().get("user")).getLocalId())).equals(((user)envelope.getExt().get("user")).getLocalId()));
    }

    @Test
    public void testDropPII() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Init();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelopeUnHashed = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, EventSensitivity.Drop);
        assert(envelope.getVer().equals("2.1"));
        assert (envelope.getName().equals(envelopeUnHashed.getName()));
        assert (!envelope.getTime().isEmpty());
        assert (envelope.getPopSample() == 10);
        assert (envelope.getSeqNum() == 0);
        assert (envelope.getIKey().equals(envelopeUnHashed.getIKey()));
        assert (envelope.getCV() == null);
        assert (envelope.getEpoch() == null);
        assert (envelope.getFlags() == 0x200101);
        assert (!envelope.getOs().isEmpty());
        assert (!envelope.getOsVer().isEmpty());
        assert (!envelope.getAppId().isEmpty());
        assert (!envelope.getAppVer().isEmpty());
        assert ((device)envelope.getExt().get("device")).getLocalId().startsWith("r:");
        assert (((user)envelope.getExt().get("user")).getLocalId() == null);
    }

    @Test
    public void testLocale() {
        CorrelationVector correlationVector = new CorrelationVector();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(((os)envelope.getExt().get("os")).getLocale().equals("en-US"));
    }

    @Test
    public void testCorrelationVectorNotAutoInit() {
        CorrelationVector correlationVector = new CorrelationVector();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(envelope.getCV() == null);
    }

    @Test
    public void testInitCorrelationVector() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Init();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(!envelope.getCV().equals("") && correlationVector.IsValid(envelope.getCV()));
    }

    @Test
    public void testInitCorrelationVectorFromIncrement() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Increment();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(!envelope.getCV().equals("") && correlationVector.IsValid(envelope.getCV()));
    }

    @Test
    public void testInitCorrelationVectorFromExtend() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.Extend();
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(!envelope.getCV().equals("") && correlationVector.IsValid(envelope.getCV()));
    }

    @Test
    public void testInitCorrelationVectorFromSet() {
        CorrelationVector correlationVector = new CorrelationVector();
        correlationVector.SetValue("AAAAAAAAAAAAAAAA.1");
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey", correlationVector);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, correlationVector.GetValue(), 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(!envelope.getCV().equals("") && correlationVector.IsValid(envelope.getCV()));
    }

    @Test
    public void testSeqNumberNotIncrementedOnDrop() {
        CustomPartA partA = new CustomPartA(new CustomLogger(), "iKey");
        assert(partA.seqCounter.get() == 0);
        Base event = (Base) EventHelper.generateBCEvent();
        Envelope envelope = partA.populateEnvelope(event, "cv", 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL, EventSensitivity.Drop);
        assert (envelope.getSeqNum() == 0);
        assert(partA.seqCounter.get() == 0);
        envelope = partA.populateEnvelope(event, "cv", 10, Cll.EventPersistence.NORMAL, Cll.EventLatency.NORMAL);
        assert(partA.seqCounter.get() == 1);
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