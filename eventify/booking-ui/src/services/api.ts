import axios from 'axios';
import qs from 'qs';
import {
  AuthResponse, LoginRequest, RegisterRequest, Event, Booking,
  CreateBookingRequest, BookingUpdateRequest, EventCreateRequest,
  EventUpdateRequest, NotificationPreferences, Pageable,
  PageableEventResponse, PageableBookingResponse,
} from '../types';

const apiInstance = axios.create({
  baseURL: 'http://localhost:8080',
  paramsSerializer: (params) => {
    return qs.stringify(params, { arrayFormat: 'repeat' });
  }
});


apiInstance.interceptors.request.use((config: any) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const apiMethods = {
  login: (data: LoginRequest) =>
    apiInstance.post<AuthResponse>('/auth/login', data).then(res => res.data),
  register: (data: RegisterRequest) =>
    apiInstance.post<AuthResponse>('/auth/register', data).then(res => res.data),

  getEvents: (pageable: Pageable, from?: string, to?: string) =>
    apiInstance.get<PageableEventResponse>('/events', {
      params: { ...pageable, from, to }
    }).then(res => res.data),

  getEvent: (id: number) =>
    apiInstance.get<Event>(`/events/${id}`).then(res => res.data),
  createEvent: (data: EventCreateRequest) =>
    apiInstance.post<Event>('/admin/events', data).then(res => res.data),
  updateEvent: (id: number, data: EventUpdateRequest) =>
    apiInstance.put<Event>(`/admin/events/${id}`, data).then(res => res.data),
  deleteEvent: (id: number) =>
    apiInstance.delete(`/admin/events/${id}`).then(res => res.data),

  getBookings: () =>
    apiInstance.get<Booking[]>('/bookings').then(res => res.data),
  getUserBookings: (pageable: Pageable) =>
    apiInstance.get<PageableBookingResponse>('/bookings/my', { params: pageable }).then(res => res.data),
  createBooking: (data: CreateBookingRequest) =>
    apiInstance.post<Booking>('/bookings', data).then(res => res.data),
  cancelBooking: (id: number) =>
    apiInstance.post(`/bookings/${id}/cancel`).then(res => res.data),
  deleteBooking: (id: number) =>
    apiInstance.delete(`/bookings/${id}`).then(res => res.data),

  getAdminBookings: (pageable: Pageable, eventId?: number, unconfirmedOnly?: boolean) =>
    apiInstance.get<PageableBookingResponse>('/admin/bookings', {
      params: { ...pageable, eventId, unconfirmedOnly }
    }).then(res => res.data),
  confirmBooking: (id: number) =>
    apiInstance.put(`/admin/bookings/${id}/confirm`).then(res => res.data),
  deleteAdminBooking: (id: number) =>
    apiInstance.delete(`/admin/bookings/${id}`).then(res => res.data),
  updateBookingStatus: (id: number, data: BookingUpdateRequest) =>
    apiInstance.patch<Booking>(`/bookings/${id}/status`, data).then(res => res.data),

  getNotificationPreferences: () =>
    apiInstance.get<NotificationPreferences>('/user/notifications').then(res => res.data),
  updateNotificationPreferences: (data: NotificationPreferences) =>
    apiInstance.put<NotificationPreferences>('/user/notifications', data).then(res => res.data),
  deleteNotificationPreferences: () =>
    apiInstance.delete('/user/notifications').then(res => res.data),

  linkTelegram: () =>
    apiInstance.post<string>('/user/telegram/link').then(res => res.data),
  unlinkTelegram: () =>
    apiInstance.delete<void>('/user/telegram/link').then(res => res.data),
};

export const api = apiMethods;
export const apiService = apiMethods;
