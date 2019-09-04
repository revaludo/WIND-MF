'''This script demonstrates how to build a variational autoencoder
with Keras and deconvolution layers.
Reference: "Auto-Encoding Variational Bayes" https://arxiv.org/abs/1312.6114
'''
import numpy as np
from scipy.stats import norm

from keras.layers import Input, Dense, Lambda, Flatten, Reshape, Layer, merge
from keras.layers import Conv2D, Conv2DTranspose
from keras.models import Model
from keras import backend as K
from keras import metrics
from keras.datasets import mnist

# input image dimensions
img_rows, img_cols, img_chns =32, 32, 3
# number of convolutional filters to use
filters = 64
# convolution kernel size
num_conv = 3


# if K.image_data_format() == 'channels_first':
#     original_img_size = (img_chns, img_rows, img_cols)
# else:
#     original_img_size = (img_rows, img_cols, img_chns)
original_dim = 3072
latent_dim = 50
intermediate_dim = 512
epsilon_std = 1.0
epochs = 5000

x = Input(shape=(img_rows, img_cols, img_chns  ))
conv_1 = Conv2D(img_chns,
                kernel_size=(2, 2),
                padding='same', activation='relu')(x)
conv_2 = Conv2D(filters,
                kernel_size=(2, 2),
                padding='same', activation='relu',
                strides=(2, 2))(conv_1)
conv_3 = Conv2D(filters,
                kernel_size=num_conv,
                padding='same', activation='relu',
                strides=1)(conv_2)
conv_4 = Conv2D(filters,
                kernel_size=num_conv,
                padding='same', activation='relu',
                strides=1)(conv_3)


shape = K.int_shape(x)

flat = Flatten()(conv_4)
hidden = Dense(intermediate_dim, activation='relu')(flat)

z_mean = Dense(latent_dim)(hidden)
z_log_var = Dense(latent_dim)(hidden)


def sampling(args):
    z_mean, z_log_var = args
    epsilon = K.random_normal(shape=(latent_dim,),
                              mean=0., stddev=epsilon_std)
    return z_mean + K.exp(z_log_var) * epsilon

# note that "output_shape" isn't necessary with the TensorFlow backend
# so you could write `Lambda(sampling)([z_mean, z_log_var])`
z = Lambda(sampling, output_shape=(latent_dim,))([z_mean, z_log_var])



# we instantiate these layers separately so as to reuse them later
decoder_hid = Dense(intermediate_dim, activation='relu')
decoder_upsample = Dense(filters * shape[2] *shape[3], activation='relu')

# if K.image_data_format() == 'channels_first':
#     output_shape = (None, filters, 16, 16)
# else:
#     output_shape = (None, 16, 16, filters)

decoder_reshape = Reshape(shape[1:])
decoder_deconv_1 = Conv2DTranspose(filters,
                                   kernel_size=num_conv,
                                   padding='same',
                                   strides=1,
                                   activation='relu')
decoder_deconv_2 = Conv2DTranspose(filters, num_conv,
                                   padding='same',
                                   strides=1,
                                   activation='relu')
# if K.image_data_format() == 'channels_first':
#     output_shape = (None, filters, 33,33)
# else:
#     output_shape = (None, 33, 33, filters)
decoder_deconv_3_upsamp = Conv2DTranspose(filters,
                                          kernel_size=(3, 3),
                                          strides=(2, 2),
                                          padding='valid',
                                          activation='relu')
decoder_mean_squash = Conv2D(img_chns,
                             kernel_size=2,
                             padding='valid',
                             activation='sigmoid')


hid_decoded = decoder_hid(z)
up_decoded = decoder_upsample(hid_decoded)
reshape_decoded = decoder_reshape(up_decoded)
deconv_1_decoded = decoder_deconv_1(reshape_decoded)
deconv_2_decoded = decoder_deconv_2(deconv_1_decoded)
x_decoded_relu = decoder_deconv_3_upsamp(deconv_2_decoded)
x_decoded_mean_squash = decoder_mean_squash(x_decoded_relu)


# Custom loss layer
class CustomVariationalLayer(Layer):
    def __init__(self, **kwargs):
        self.is_placeholder = True
        super(CustomVariationalLayer, self).__init__(**kwargs)

    def vae_loss(self, x, x_decoded_mean_squash):
        x = K.flatten(x)
        x_decoded_mean_squash = K.flatten(x_decoded_mean_squash)
        xent_loss = img_rows * img_cols * metrics.binary_crossentropy(x, x_decoded_mean_squash)
        kl_loss = - 0.5 * K.mean(1 + z_log_var - K.square(z_mean) - K.exp(z_log_var), axis=-1)
        return K.mean(xent_loss + kl_loss)

    def call(self, inputs):
        x = inputs[0]
        x_decoded_mean_squash = inputs[1]
        loss = self.vae_loss(x, x_decoded_mean_squash)
        self.add_loss(loss, inputs=inputs)
        # We don't use this output.
        return x


y = CustomVariationalLayer()([x, x_decoded_mean_squash])
vae = Model(x, y)
vae.compile(optimizer='rmsprop', loss=None)
vae.summary()

# train the VAE on MNIST digits
#(x_train, _), (x_test, y_test) = mnist.load_data()

# x_train = x_train.astype('float32') / 255.
# x_train = x_train.reshape((x_train.shape[0],) + original_img_size)
# x_test = x_test.astype('float32') / 255.
# x_test = x_test.reshape((x_test.shape[0],) + original_img_size)
#
# print('x_train.shape:', x_train.shape)


x_train=np.load("data/train.npy")
x_train = x_train.astype('float32') / 255.
x_train = x_train.reshape((x_train.shape[0],) +original_img_size )
x_test=np.load("data/test.npy")
x_test = x_test.astype('float32') / 255.
x_test = x_test.reshape((x_test.shape[0],) +original_img_size )
print('x_train.shape:', x_train.shape)
vae.fit(x_train,
        shuffle=True,
        epochs=epochs,
        batch_size=batch_size,
        validation_data=(x_test, x_test))
# build a model to project inputs on the latent space
encoder = Model(x, z_mean)
x_train_encoded = encoder.predict(x_train, batch_size=batch_size)
x_test_encoded = encoder.predict(x_test, batch_size=batch_size)
np.savetxt('data/trainencoder.txt', x_train_encoded)
np.savetxt('data/testencoder.txt', x_test_encoded)