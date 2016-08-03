package se.kth.ics.pwnpr3d.datatypes;

public enum EncryptionType {
    None (1.0, 1.0, 1.0, 0),
    Safe (0.0, 0.0, 0.0, Integer.MAX_VALUE),

    AES (0.01, 0.01, 0.01, 42),
    RC4 (0.1, 0.1, 0.1, 42),
    DES (0.2, 0.2, 0.2, 42);

    private final double pCompromisedIntegrity;
    private final double pCompromisedConfidentiality;
    private final double pCompromisedAvailability;
    private final int    keyBitsOfEntropy;

    EncryptionType(double pCompromisedIntegrity, double pCompromisedConfidentiality, double pCompromisedAvailability, int keyBitsOfEntropy) {
        this.pCompromisedIntegrity = pCompromisedIntegrity;
        this.pCompromisedConfidentiality = pCompromisedConfidentiality;
        this.pCompromisedAvailability = pCompromisedAvailability;
        this.keyBitsOfEntropy = keyBitsOfEntropy;
    }

    public double getpCompromisedIntegrity() {
        return pCompromisedIntegrity;
    }

    public double getpCompromisedConfidentiality() {
        return pCompromisedConfidentiality;
    }

    public double getpCompromisedAvailability() {
        return pCompromisedAvailability;
    }

    public int getKeyBitsOfEntropy() {
        return keyBitsOfEntropy;
    }
}
