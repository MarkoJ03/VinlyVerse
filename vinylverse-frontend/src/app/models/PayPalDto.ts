export interface PayPalCreateOrderResponse {
  paypalOrderId: string;
  approveUrl: string;
  status: string;
}

export interface PayPalCaptureResponse {
  narudzbinaId: number;
  status: string;
  providerOrderId: string;
}
