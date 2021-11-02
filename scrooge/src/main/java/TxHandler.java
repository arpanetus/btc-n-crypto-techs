import java.util.ArrayList;

public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.pool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        
        ArrayList<UTXO> currentUtxos = new ArrayList<UTXO>();

        double inputSum = 0, outputSum = 0;

        for (Transaction.Output output:tx.getOutputs()) {
            if (output.value < 0) return false; // (4)
            outputSum += output.value;            
        }

        
        for (Transaction.Input input:tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            
            if (!pool.contains(utxo)) return false; // (1)
            
            if (currentUtxos.contains(utxo)) return false; // (3)
            
            currentUtxos.add(utxo);

            inputSum += pool.getTxOutput(utxo).value;

            int idx = input.outputIndex;

            if (!pool.getTxOutput(utxo).address.verifySignature(tx.getRawDataToSign(idx), input.signature)) return false; // (2)

        }

        if (inputSum < outputSum) return false; // (5)

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        ArrayList<Transaction> validTxs = new ArrayList<Transaction>();

        for (Transaction txn : possibleTxs) {
            if(this.isValidTx(txn)){
                validTxs.add(txn);
                
                for (Transaction.Input input:txn.getInputs()) {
                    UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                    pool.removeUTXO(utxo);
                }
                
                for (Transaction.Output output:txn.getOutputs()) {
                    UTXO utxo = new UTXO(txn.getHash(), output.index);
                    pool.addUTXO(utxo, output);
                }
            }
        }

        return validTxs.toArray();
    }

}
