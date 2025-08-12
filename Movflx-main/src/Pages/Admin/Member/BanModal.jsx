import React from "react";

const BanModal = ({ show, onClose, onSubmit, banMessage, setBanMessage, error, setError }) => {
    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-white p-6 rounded-lg shadow-lg w-[32rem]">
                <h2 className="text-lg font-bold mb-4">Enter Ban Message</h2>
                <textarea
                    className="w-full p-2 border rounded-lg mb-4"
                    rows="6"
                    value={banMessage}
                    onChange={(e) => {
                        setBanMessage(e.target.value);
                        setError("");
                    }}
                    placeholder="Type the reason for banning..."
                />
                {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
                <div className="flex justify-end gap-2">
                    <button
                        className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
                        onClick={onClose}
                    >
                        Cancel
                    </button>
                    <button
                        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                        onClick={onSubmit}
                    >
                        Submit
                    </button>
                </div>
            </div>
        </div>
    );
};

export default BanModal;