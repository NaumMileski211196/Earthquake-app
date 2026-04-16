import { useEffect, useState } from "react";
import "./index.css";

import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

// FIX Leaflet icons (MORA)
delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
    iconUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    shadowUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

function App() {
    const [earthquakes, setEarthquakes] = useState([]);
    const [search, setSearch] = useState("");
    const [sortType, setSortType] = useState("date-desc");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch("http://localhost:8080/api/earthquakes")
            .then((res) => {
                if (!res.ok) throw new Error("API error");
                return res.json();
            })
            .then((data) => {
                setEarthquakes(data);
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, []);

    // SEARCH
    const filtered = earthquakes.filter((eq) =>
        (eq.location || eq.place || "")
            .toLowerCase()
            .includes(search.toLowerCase())
    );

    // SORT
    const sortedEarthquakes = [...filtered].sort((a, b) => {
        const magA = a.magnitude || a.mag || 0;
        const magB = b.magnitude || b.mag || 0;

        const dateA = new Date(a.date || a.time);
        const dateB = new Date(b.date || b.time);

        switch (sortType) {
            case "mag-desc":
                return magB - magA;
            case "mag-asc":
                return magA - magB;
            case "date-desc":
                return dateB - dateA;
            case "date-asc":
                return dateA - dateB;
            default:
                return 0;
        }
    });

    return (
        <div className="container">
            <h1 className="title">🌍 Earthquake Dashboard</h1>

            {/* SEARCH */}
            <input
                type="text"
                placeholder="Search location..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
            />

            {/* SORT */}
            <select
                value={sortType}
                onChange={(e) => setSortType(e.target.value)}
            >
                <option value="date-desc">Newest first</option>
                <option value="date-asc">Oldest first</option>
                <option value="mag-desc">Highest magnitude</option>
                <option value="mag-asc">Lowest magnitude</option>
            </select>

            {/* LOADING */}
            {loading && <p>Loading data...</p>}

            {/* ERROR */}
            {error && <p style={{ color: "red" }}>{error}</p>}

            {/* TABLE */}
            {!loading && !error && (
                <>
                    <div className="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>Location</th>
                                <th>Magnitude</th>
                                <th>Date</th>
                            </tr>
                            </thead>

                            <tbody>
                            {sortedEarthquakes.map((eq, index) => (
                                <tr key={eq.id || index}>
                                    <td>{eq.location || eq.place || "-"}</td>

                                    <td>
                                        {eq.magnitude || eq.mag || "-"}
                                    </td>

                                    <td>
                                        {new Date(
                                            eq.time || eq.date
                                        ).toLocaleString()}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>

                    {/* 🌍 MAP */}
                    <MapContainer center={[20, 0]} zoom={2}>
                        <TileLayer
                            attribution="&copy; OpenStreetMap contributors"
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        />

                        {sortedEarthquakes.map((eq, index) => {
                            const lat = eq.latitude || eq.lat;
                            const lng = eq.longitude || eq.lon;

                            if (!lat || !lng) return null;

                            return (
                                <Marker
                                    key={eq.id || index}
                                    position={[lat, lng]}
                                >
                                    <Popup>
                                        <b>{eq.place}</b>
                                        <br />
                                        Magnitude:{" "}
                                        {eq.magnitude || eq.mag}
                                        <br />
                                        {new Date(
                                            eq.time || eq.date
                                        ).toLocaleString()}
                                    </Popup>
                                </Marker>
                            );
                        })}
                    </MapContainer>
                </>
            )}
        </div>
    );
}

export default App;