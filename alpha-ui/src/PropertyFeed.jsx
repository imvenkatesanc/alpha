import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { faHeart, faShoppingCart, faArrowLeft, faShareAlt, faComment } from '@fortawesome/free-solid-svg-icons';
import PropertyDetails from './PropertyDetails'; // Import the new component

const PropertyFeed = () => {
  const [properties, setProperties] = useState([]);
  const [filteredProperties, setFilteredProperties] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [images, setImages] = useState({});
  const [selectedProperty, setSelectedProperty] = useState(null); // State for the selected property

  useEffect(() => {
    const fetchProperties = async () => {
      setLoading(true);
      setError('');

      try {
        const token = localStorage.getItem('authToken');
        if (!token) {
          throw new Error('No auth token found');
        }

        const response = await axios.get('http://localhost:8080/api/properties/all', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setProperties(response.data.properties);
        setFilteredProperties(response.data.properties);
      } catch (err) {
        setError('Failed to fetch properties.');
        console.error('Error fetching properties', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProperties();
  }, []);

  useEffect(() => {
    const loadImages = async () => {
      const token = localStorage.getItem('authToken');
      if (!token) {
        console.error('No auth token found');
        setError('No auth token found');
        return;
      }

      if (filteredProperties.length > 0) {
        for (const property of filteredProperties) {
          try {
            const response = await axios.get(property.downloadURL, {
              responseType: 'arraybuffer',
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/octet-stream',
              },
            });

            const base64Image = btoa(
              new Uint8Array(response.data).reduce((data, byte) => {
                return data + String.fromCharCode(byte);
              }, '')
            );
            const imageUrl = `data:image/jpeg;base64,${base64Image}`;
            setImages((prevImages) => ({ ...prevImages, [property.propertyName]: imageUrl }));
          } catch (error) {
            console.error('Failed to load image:', error);
            setError('Failed to load image. Please check your token and URL.');
          }
        }
      }
    };

    loadImages();
  }, [filteredProperties]);

  const handleSearch = () => {
    const filtered = properties.filter((property) =>
      property.propertyName.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredProperties(filtered);
    setImages({});
  };

  const handlePropertyClick = async (property) => {
    setLoading(true); // Start loading state
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        throw new Error('No auth token found');
      }

      // Fetch the image data
      const response = await axios.get(property.downloadURL, {
        responseType: 'arraybuffer',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/octet-stream',
        },
      });

      const base64Image = btoa(
        new Uint8Array(response.data).reduce((data, byte) => {
          return data + String.fromCharCode(byte);
        }, '')
      );
      const imageUrl = `data:image/jpeg;base64,${base64Image}`;

      // Set the selected property with the image data
      setSelectedProperty({ ...property, imageData: imageUrl });
      document.body.style.overflow = 'hidden'; // Prevent scrolling
    } catch (error) {
      console.error('Failed to load image:', error);
      setError('Failed to load image. Please check your token and URL.');
    } finally {
      setLoading(false); // End loading state
    }
  };

  const closeDetails = () => {
    setSelectedProperty(null);
    document.body.style.overflow = ''; // Restore scrolling
  };

  return (
    <>
      <div className="search-container">
        <input
          type="text"
          placeholder="Search by property name..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="search-bar"
        />
        <button onClick={handleSearch} className="search-button">
          Search &nbsp; &nbsp;
          <FontAwesomeIcon icon={faSearch} />
        </button>
      </div>
      <div className="properties-container">
        {error && <p className="error">{error}</p>}
        {loading ? (
          <p>Loading...</p>
        ) : (
          filteredProperties.map((property) => (
            <div key={property.name} className="property-card" onClick={() => handlePropertyClick(property)}>
              <h2>{property.name}</h2>
              <p>{property.description}</p>
              <p><strong>Price:</strong> ${property.price.toLocaleString()}</p>
              <p><strong>Type:</strong> {property.type}</p>
              <p><strong>Address:</strong> {property.address}</p>
              <p><strong>Availability:</strong> {property.available ? 'Yes' : 'No'}</p>
              {images[property.propertyName] && (
                <img
                  src={images[property.propertyName]}
                  alt={property.propertyName}
                  style={{ maxWidth: '100%', height: 'auto' }}
                />
              )}
            </div>
          ))
        )}
      </div>
      {selectedProperty && (
        <PropertyDetails property={selectedProperty} onClose={closeDetails} />
      )}
    </>
  );
};

export default PropertyFeed;