import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart, faShoppingCart, faArrowLeft, faShareAlt, faComment } from '@fortawesome/free-solid-svg-icons';

const PropertyDetails = ({ property, onClose }) => {
    const [likes, setLikes] = useState(0);
    const [comment, setComment] = useState('');
    const [comments, setComments] = useState([]);
    const [showComments, setShowComments] = useState(false);
    const [loading, setLoading] = useState(true); // Loading state for the image
    const [imageError, setImageError] = useState(false); // Error state for the image

    const shareCount = 23; // Example share count

    const handleLike = () => {
        setLikes(likes + 1);
    };

    const handleCommentSubmit = (e) => {
        e.preventDefault();
        if (comment.trim()) {
            setComments([...comments, comment]);
            setComment('');
        }
    };

    const toggleComments = () => {
        setShowComments((prev) => !prev);
    };

    if (!property) return null;

    return (
        <div className="property-details">
            <style>
                {`
        //   .property-details {
        //     width: 1180px;
        //     padding: 25px;
        //     position: fixed;
        //     overflow-y: auto;
        //     max-height: calc(100vh - 90px);
        //     z-index: 1000; /* Ensure it is on top of other content */
        //     top: 90px;
        //     bottom: 0;
        //     right: 0;
        //     background: white;
        //     overflow: hidden;
        //     border-radius: 12px;
        //     border: 2px solid whitesmoke;
        //   }
                .property-details {
      width: 1180px;
      padding: 25px;
      position: fixed;
      overflow-y: auto; /* Allow vertical scrolling */
      max-height: calc(100vh - 90px);
      z-index: 1000; /* Ensure it is on top of other content */
      top: 90px;
      bottom: 0;
      right: 0;
      background: white;
      border-radius: 12px;
      border: 2px solid whitesmoke;
    }

          .close-button {
            color: black;
            border: none;
            font-size: 25px;
            padding: 10px;
            cursor: pointer;
          }
          .close-button:hover {
            color: #FF1D8D;
          }
          .actions {
            margin: 20px 0;
            display: flex;
            justify-content: space-between;
          }
          .buy-button {
            color: white;
            background-color: grey;
            border-radius: 5px;
            border: none;
            padding: 10px;
            cursor: pointer;
          }
          .buy-button:hover {
            background-color: #FF1D8D;
          }
          .comments-section {
            margin-top: 30px;
          }
          .comments-section > form > button {
            max-width: 120px;
            margin-left: -120px;
            background-color: gray;
          }
          .comments-section > form > button:hover {
            background-color: #FF1D8D;
          }
          .comments-section ul {
            list-style: none;
            padding: 0;
          }
          .comments-section li {
            margin: 5px 0;
          }
          .icon {
            color: gray;
            cursor: pointer;
            margin-right: 50px;
          }
          .icon:hover {
            color: #FF1D8D;
          }
          textarea {
            max-width: 1000px;
            margin-bottom: 30px;
          }
          .loading {
            text-align: center;
            font-size: 16px;
            color: gray;
          }
        `}
            </style>

            <span onClick={onClose} className="close-button">
                <FontAwesomeIcon icon={faArrowLeft} />
            </span>
            <h2>{property.name}</h2>
            <p><strong>Price:</strong> ${property.price.toLocaleString()}</p>
            <p><strong>Details:</strong> {property.description}</p>
            <p><strong>Type:</strong> {property.type}</p>
            <p><strong>Address:</strong> {property.address}</p>
            <p><strong>Availability:</strong> {property.available ? 'Yes' : 'No'}</p>
            {property.imageData ? (
                <img
                    src={property.imageData}
                    alt={property.name}
                    className="property-image"
                    style={{ maxWidth: '100%', height: 'auto' }}
                />
            ) : (
                <p>Image not available</p>
            )}
            <div className="actions">
                <div className="buy-button">Buy Now</div>
            </div>
            <div className="icons">
                <span onClick={handleLike} className="icon">
                    <FontAwesomeIcon icon={faHeart} /> {likes}
                </span>
                <span className="icon">
                    <FontAwesomeIcon icon={faShareAlt} /> {shareCount}
                </span>
                <span onClick={toggleComments} className="icon">
                    <FontAwesomeIcon icon={faComment} /> {comments.length}
                </span>
            </div>

            {showComments && (
                <div className="comments-section">
                    <h3>Comments</h3>
                    <form onSubmit={handleCommentSubmit}>
                        <textarea
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            placeholder="Add a comment..."
                            rows="3"
                        />
                        <button type="submit">Post Comment</button>
                    </form>
                    <ul>
                        {comments.map((c, index) => (
                            <li key={index}>{c}</li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default PropertyDetails;
