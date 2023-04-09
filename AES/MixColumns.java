public class MixColumns {

    int Nb = 4;

    private final byte[][] Matrix = {
            {0x02, 0x03, 0x01, 0x01},
            {0x01, 0x02, 0x03, 0x01},
            {0x01, 0x01, 0x02, 0x03},
            {0x03, 0x01, 0x01, 0x02}
    };

    private final byte[][] invMatrix = {
            {0x0E, 0x0B, 0x0D, 0x09},
            {0x09, 0x0E, 0x0B, 0x0D},
            {0x0D, 0x09, 0x0E, 0x0B},
            {0x0B, 0x0D, 0x09, 0x0E}
    };

    public byte multiply(byte firstByte, byte secondByte) {
        byte result = 0;
        byte temp;
        while (firstByte != 0) {
            if ((firstByte & 1) != 0) {
                result = (byte) (result ^ secondByte);
            }
            temp = (byte) (secondByte & 0x80);
            secondByte = (byte) (secondByte << 1);
            if (temp != 0) {
                secondByte = (byte) (secondByte ^ 0x1B);
            }
            firstByte = (byte) ((firstByte & 0xFF) >> 1);
        }
        return result;
    }

    public byte[][] mixColumns(byte[][] state, boolean encryption) {
        int[] temp = new int[4];
        if (encryption) {
            for (int i = 0; i < Nb; i++) {
                temp[0] = multiply(Matrix[0][0], state[0][i]) ^ multiply(Matrix[0][1], state[1][i]) ^
                        multiply(Matrix[0][2], state[2][i]) ^ multiply(Matrix[0][3], state[3][i]);
                temp[1] = multiply(Matrix[1][0], state[0][i]) ^ multiply(Matrix[1][1], state[1][i]) ^
                        multiply(Matrix[1][2], state[2][i]) ^ multiply(Matrix[1][3], state[3][i]);
                temp[2] = multiply(Matrix[2][0], state[0][i]) ^ multiply(Matrix[2][1], state[1][i]) ^
                        multiply(Matrix[2][2], state[2][i]) ^ multiply(Matrix[2][3], state[3][i]);
                temp[3] = multiply(Matrix[3][0], state[0][i]) ^ multiply(Matrix[3][1], state[1][i]) ^
                        multiply(Matrix[3][2], state[2][i]) ^ multiply(Matrix[3][3], state[3][i]);
                for (int j = 0; j < 4; j++) {
                    state[j][i] = (byte) temp[j];
                }
            }
        } else {
            for (int i = 0; i < Nb; i++) {
                temp[0] = multiply(invMatrix[0][0], state[0][i]) ^ multiply(invMatrix[0][1], state[1][i]) ^
                        multiply(invMatrix[0][2], state[2][i]) ^ multiply(invMatrix[0][3], state[3][i]);
                temp[1] = multiply(invMatrix[1][0], state[0][i]) ^ multiply(invMatrix[1][1], state[1][i]) ^
                        multiply(invMatrix[1][2], state[2][i]) ^ multiply(invMatrix[1][3], state[3][i]);
                temp[2] = multiply(invMatrix[2][0], state[0][i]) ^ multiply(invMatrix[2][1], state[1][i]) ^
                        multiply(invMatrix[2][2], state[2][i]) ^ multiply(invMatrix[2][3], state[3][i]);
                temp[3] = multiply(invMatrix[3][0], state[0][i]) ^ multiply(invMatrix[3][1], state[1][i]) ^
                        multiply(invMatrix[3][2], state[2][i]) ^ multiply(invMatrix[3][3], state[3][i]);
                for (int j = 0; j < 4; j++) {
                    state[j][i] = (byte) temp[j];
                }

            }
        }

        return state;
    }
}
