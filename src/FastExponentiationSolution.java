import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class solves the Towers problem using Fast Exponentiation from
 * HackerRank problem set. It is a part of B503 project.
 * 
 * @author jaynagle
 *
 */
public class FastExponentiationSolution {

	/**
	 * MOD is a constant used to limit the answer to a scaled value by a factor
	 * of 10^7+7.
	 */
	private static final BigInteger MOD = BigInteger.valueOf(1000000007);

	/**
	 * brickIndicatorVector stores which bricks are available as boolean flags.
	 */
	private BigInteger[] brickIndicatorVector = new BigInteger[17];

	/**
	 * vector stores values number of towers up to height 16 for given set of
	 * bricks which is calculated using dynamic programming.
	 */
	private BigInteger[] vector = new BigInteger[17];

	/**
	 * Main matrix used for calculation of high value inputs.
	 */
	private BigInteger[][] matrix = new BigInteger[16][16];

	/**
	 * Main method which launches the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FastExponentiationSolution solution = new FastExponentiationSolution();
		solution.matrixCalculation(solution.readInput());
	}

	/**
	 * This method reads input attributes from STDIN.
	 * 
	 * @return
	 */
	private List<String> readInput() {
		List<String> inputList = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;

			while ((input = br.readLine()) != null) {
				inputList.add(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputList;
	}

	/**
	 * This method performs necessary matrix calculations to find the final
	 * answer (Number of possible towers).
	 * 
	 * @param inputList
	 */
	private void matrixCalculation(List<String> inputList) {
		BigInteger towerHeight = new BigInteger(inputList.get(0));
		int numOfDistinctBricks = Integer.parseInt(inputList.get(1));
		String[] brickHeightsStringArray = inputList.get(2).split(" ");

		int[] brickHeightsIntArray = new int[brickHeightsStringArray.length];
		for (int i = 0; i < brickHeightsStringArray.length; i++) {
			brickHeightsIntArray[i] = Integer.parseInt(brickHeightsStringArray[i]);
		}

		/**
		 * This block initializes the main matrix used to calculate final
		 * answer.
		 */
		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				if (i + 1 == j) {
					matrix[i][j] = BigInteger.valueOf(1);
				} else {
					matrix[i][j] = BigInteger.valueOf(0);
				}
			}
		}

		/**
		 * This block initializes multiplication vector and height flag array.
		 */
		for (int i = 0; i < 17; ++i) {
			for (int j = 0; j < 17; ++j) {
				vector[i] = BigInteger.valueOf(0);
				brickIndicatorVector[i] = BigInteger.valueOf(0);
			}
		}

		for (int i = 0; i < numOfDistinctBricks; i++) {
			matrix[15][15 - brickHeightsIntArray[i] + 1] = BigInteger.valueOf(1);
			brickIndicatorVector[brickHeightsIntArray[i] - 1] = BigInteger.valueOf(1);
		}

		/**
		 * createVector method calculates and populates values of number of
		 * towers up to height 16.
		 */
		createVector(brickIndicatorVector, BigInteger.valueOf(17));

		/**
		 * This line rotates the array to eliminate f(0) value for number of
		 * towers.
		 */
		vector = Arrays.copyOfRange(vector, 1, 17);

		BigInteger result = BigInteger.valueOf(0);
		if (towerHeight.compareTo(BigInteger.valueOf(16L)) <= 0) {
			/**
			 * This block pulls out values for number of towers up to height 16.
			 */
			result = (vector[towerHeight.subtract(BigInteger.valueOf(1)).intValue()]).multiply(BigInteger.valueOf(2))
					.mod(MOD);
		} else {
			/**
			 * This block computes values for number of towers greater than 16.
			 */
			BigInteger[][] R = getExponent(matrix, towerHeight.subtract(BigInteger.valueOf(16L)));
			for (int j = 0; j < 16; j++) {
				result = (result.add(R[15][j].multiply(vector[j]))).mod(MOD);
			}
			result = result.multiply(BigInteger.valueOf(2)).mod(MOD);
		}

		submitOutput(result);
	}

	/**
	 * This method publishes the computed output on STDOUT.
	 * 
	 * @param result
	 */
	private void submitOutput(BigInteger result) {
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));
			output.write(String.valueOf(result));
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method calculates multiplication of 2 input matrices using Fast
	 * Exponentiation.
	 * 
	 * @param matrix
	 * @param power
	 * @return
	 */
	public BigInteger[][] getExponent(BigInteger[][] matrix, BigInteger power) {
		if (power.compareTo(BigInteger.valueOf(0L)) == 0) {
			BigInteger[][] identityMatrix = new BigInteger[16][16];
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (i == j)
						identityMatrix[i][j] = BigInteger.valueOf(1);
					else
						identityMatrix[i][j] = BigInteger.valueOf(0);
				}
			}
			return identityMatrix;
		} else if (power.mod(BigInteger.valueOf(2L)) == BigInteger.valueOf(0L)) {
			BigInteger[][] R = getExponent(matrix, power.divide(BigInteger.valueOf(2L)));
			return multiply(R, R);
		} else {
			BigInteger[][] R = getExponent(matrix, power.subtract(BigInteger.valueOf(1L)));
			return multiply(R, matrix);
		}
	}

	/**
	 * This method calculates multiplication of 2 input identity matrices.
	 * 
	 * @param matrix1
	 * @param matrix2
	 * @return
	 */
	public BigInteger[][] multiply(BigInteger[][] matrix1, BigInteger[][] matrix2) {
		BigInteger[][] result = new BigInteger[16][16];

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				result[i][j] = BigInteger.valueOf(0);
				for (int k = 0; k < 16; k++) {
					result[i][j] = (result[i][j].add(matrix1[i][k].multiply(matrix2[k][j]))).mod(MOD);
				}
			}
		}
		return result;
	}

	/**
	 * This method computes values of number of towers for height up to 16.
	 * 
	 * @param brickIndicatorVector
	 * @param towerHeight
	 * @return
	 */
	private BigInteger createVector(BigInteger[] brickIndicatorVector, BigInteger towerHeight) {
		if (towerHeight.compareTo(BigInteger.valueOf(0)) == 0) {
			return BigInteger.valueOf(1);
		}
		BigInteger k = createVector(brickIndicatorVector, towerHeight.subtract(BigInteger.valueOf(1)));
		vector[towerHeight.subtract(BigInteger.valueOf(1)).intValue()] = k;

		int j = towerHeight.intValue();
		BigInteger tmpHeight = BigInteger.valueOf(0);
		while (j > 0) {
			tmpHeight = tmpHeight.add(brickIndicatorVector[j - 1]
					.multiply(vector[towerHeight.subtract(BigInteger.valueOf(j)).intValue()]));
			j--;
		}
		return tmpHeight;
	}
}